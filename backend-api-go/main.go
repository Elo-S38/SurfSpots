package main // 📦 Point d'entrée principal du programme

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"strconv"

	"github.com/joho/godotenv"      // 🌿 Pour charger le fichier .env
	_ "github.com/go-sql-driver/mysql" // 🧩 Driver MySQL
	"github.com/gorilla/mux"           // 🐵 Gestion des routes dynamiques
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

var db *sql.DB // 🔌 Connexion à la base

func init() {
	fmt.Println("🔐 Chargement des variables d'environnement...")

	// ✅ Charge le fichier config.env
	err := godotenv.Load("config.env")
	if err != nil {
		log.Fatal("❌ Erreur de chargement du fichier config.env :", err)
	}

	// 📦 Récupération des infos de connexion depuis les variables d'environnement
	user := os.Getenv("DB_USER")
	pass := os.Getenv("DB_PASS")
	host := os.Getenv("DB_HOST")
	port := os.Getenv("DB_PORT")
	name := os.Getenv("DB_NAME")

	// 🔗 Création du DSN (Data Source Name)
	dsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s", user, pass, host, port, name)

	// 📡 Connexion à la base MySQL
	db, err = sql.Open("mysql", dsn)
	if err != nil {
		log.Fatal("❌ Erreur lors de sql.Open :", err)
	}

	// 🔁 Vérifie la connexion
	if err = db.Ping(); err != nil {
		log.Fatal("❌ Impossible de se connecter à la base :", err)
	}

	fmt.Println("✅ Connexion à la base réussie")
}

// 🌐 GET /api/spots
func GetSpots(w http.ResponseWriter, r *http.Request) {
	rows, err := db.Query("SELECT * FROM spots")
	if err != nil {
		http.Error(w, "Erreur SQL", http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var spots []DataSpot
	for rows.Next() {
		var s DataSpot
		err := rows.Scan(&s.ID, &s.Name, &s.SurfBreak, &s.Photo, &s.Address, &s.Difficulty, &s.SeasonStart, &s.SeasonEnd, &s.Rating)
		if err != nil {
			http.Error(w, "Erreur lecture", http.StatusInternalServerError)
			return
		}
		spots = append(spots, s)
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(spots)
}

// 🌐 GET /api/spots/{id}
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

// 🌐 PUT /api/spots/{id}
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

// 🌐 POST /api/spots
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

// 🚀 main : démarrage du serveur
func main() {

	fmt.Println("Connexion à la base de données...")

	// Récupération du mot de passe depuis les variables d'environnement
	pswd := ""

	// Connexion à la base MySQL
	db, err := sql.Open("mysql", "root:"+pswd+"@tcp(localhost:3306)/surfspot")
	if err != nil {
		log.Fatal("Erreur lors de sql.Open :", err)
	}
	defer db.Close()

	err = db.Ping()
	if err != nil {
		log.Fatal("Erreur de connexion à la base :", err)
	}
	fmt.Println("Connexion à la base réussie")

	// Exemple d'insertion
	insert, err := db.Query("INSERT INTO `surfspot`.`spots` ( `name`, `surfBreak`, `photo`, `address`, `difficulty`, `seasonStart`, `seasonEnd`, `rating`) VALUES ('Carl', 'Point Break', 'https://example.com/pipeline.jpg', 'aaa', '5', '2025-07-01', '2025-07-02', '0');")
	if err != nil {
		log.Fatal(err)
	}
	defer insert.Close()

	fmt.Println("Insertion réussie")

	// Configuration du routeur
