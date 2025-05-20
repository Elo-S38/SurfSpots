package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"strconv"

	_ "github.com/go-sql-driver/mysql"
	"github.com/gorilla/mux"
	"github.com/joho/godotenv"
)

// 🎯 Structure représentant un spot
type DataSpot struct {
	ID          int    `json:"id"`
	Name        string `json:"name"`
	SurfBreak   string `json:"surfBreak"`
	Photo       string `json:"photo"`
	Address     string `json:"address"`
	Difficulty  int    `json:"difficulty"`
	SeasonStart string `json:"seasonStart"`
	SeasonEnd   string `json:"seasonEnd"`
	Rating      int    `json:"rating"`
}

var db *sql.DB

// 🔐 Connexion à la base à l'initialisation
func init() {
	fmt.Println("🔐 Chargement des variables d'environnement...")
	err := godotenv.Load("config.env")
	if err != nil {
		log.Fatal("❌ Erreur config.env :", err)
	}

	user := os.Getenv("DB_USER")
	pass := os.Getenv("DB_PASS")
	host := os.Getenv("DB_HOST")
	port := os.Getenv("DB_PORT")
	name := os.Getenv("DB_NAME")

	dsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s", user, pass, host, port, name)

	db, err = sql.Open("mysql", dsn)
	if err != nil {
		log.Fatal("❌ Erreur sql.Open :", err)
	}

	if err = db.Ping(); err != nil {
		log.Fatal("❌ Connexion échouée :", err)
	}

	fmt.Println("✅ Connexion à la base réussie")
}

// 📦 GET /api/spots?page=1&limit=10&location=bordeaux
func GetSpots(w http.ResponseWriter, r *http.Request) {
	pageStr := r.URL.Query().Get("page")
	limitStr := r.URL.Query().Get("limit")
	location := r.URL.Query().Get("location")

	page, err := strconv.Atoi(pageStr)
	if err != nil || page < 1 {
		page = 1
	}
	limit, err := strconv.Atoi(limitStr)
	if err != nil || limit < 1 {
		limit = 10
	}
	offset := (page - 1) * limit

	// 🧠 Construction de la requête avec filtrage dynamique
	query := "SELECT * FROM spots WHERE 1=1"
	params := []interface{}{}

	if location != "" {
		query += " AND address LIKE ?"
		params = append(params, "%"+location+"%")
	}

	query += " LIMIT ? OFFSET ?"
	params = append(params, limit, offset)

	rows, err := db.Query(query, params...)
	if err != nil {
		http.Error(w, "Erreur SQL", http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var spots []DataSpot
	for rows.Next() {
		var s DataSpot
		if err := rows.Scan(&s.ID, &s.Name, &s.SurfBreak, &s.Photo, &s.Address, &s.Difficulty, &s.SeasonStart, &s.SeasonEnd, &s.Rating); err != nil {
			http.Error(w, "Erreur lecture", http.StatusInternalServerError)
			return
		}
		spots = append(spots, s)
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"page":  page,
		"limit": limit,
		"data":  spots,
	})
}

// 📦 GET /api/spots/{id}
func GetSpotByID(w http.ResponseWriter, r *http.Request) {
	idStr := mux.Vars(r)["id"]
	id, err := strconv.Atoi(idStr)
	if err != nil {
		http.Error(w, "ID invalide", http.StatusBadRequest)
		return
	}

	row := db.QueryRow("SELECT * FROM spots WHERE id = ?", id)
	var s DataSpot
	if err := row.Scan(&s.ID, &s.Name, &s.SurfBreak, &s.Photo, &s.Address, &s.Difficulty, &s.SeasonStart, &s.SeasonEnd, &s.Rating); err != nil {
		http.Error(w, "Spot non trouvé", http.StatusNotFound)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(s)
}

// 📦 PUT /api/spots/{id}
func UpdateSpotRating(w http.ResponseWriter, r *http.Request) {
	idStr := mux.Vars(r)["id"]
	id, err := strconv.Atoi(idStr)
	if err != nil {
		http.Error(w, "ID invalide", http.StatusBadRequest)
		return
	}

	var payload struct {
		Rating int `json:"rating"`
	}
	if err := json.NewDecoder(r.Body).Decode(&payload); err != nil {
		http.Error(w, "Corps JSON invalide", http.StatusBadRequest)
		return
	}

	_, err = db.Exec("UPDATE spots SET rating = ? WHERE id = ?", payload.Rating, id)
	if err != nil {
		http.Error(w, "Erreur update", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusNoContent)
}

// 📦 POST /api/spots
func CreateSpot(w http.ResponseWriter, r *http.Request) {
	var s DataSpot
	if err := json.NewDecoder(r.Body).Decode(&s); err != nil {
		http.Error(w, "JSON invalide", http.StatusBadRequest)
		return
	}

	res, err := db.Exec("INSERT INTO spots (name, surfBreak, photo, address, difficulty, seasonStart, seasonEnd, rating) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
		s.Name, s.SurfBreak, s.Photo, s.Address, s.Difficulty, s.SeasonStart, s.SeasonEnd, s.Rating)
	if err != nil {
		http.Error(w, "Erreur insertion", http.StatusInternalServerError)
		return
	}

	id, _ := res.LastInsertId()
	s.ID = int(id)

	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(s)
}

// 🚀 Démarrage du serveur
func main() {
	r := mux.NewRouter()

	r.HandleFunc("/api/spots", GetSpots).Methods("GET")
	r.HandleFunc("/api/spots/{id}", GetSpotByID).Methods("GET")
	r.HandleFunc("/api/spots/{id}", UpdateSpotRating).Methods("PUT")
	r.HandleFunc("/api/spots", CreateSpot).Methods("POST")

	log.Println("🌍 Serveur en écoute sur http://localhost:8080")
	log.Fatal(http.ListenAndServe(":8080", r))
}
