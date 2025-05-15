package main // 📦 Point d'entrée principal du programme

import (
	"database/sql"
	"encoding/json" // 📦 Pour encoder/décoder les données en JSON
	"fmt"
	"log"      // 📝 Pour afficher des messages dans le terminal
	"net/http" // 🌐 Pour gérer les requêtes et les réponses HTTP
	"strconv"  // 🔢 Pour convertir des chaînes de caractères en entiers

	_ "github.com/go-sql-driver/mysql"
	"github.com/gorilla/mux" // 🐵 Librairie externe pour gérer les routes dynamiques
)

// 🎯 Définition de la structure de données envoyée à l’application mobile
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

// 💾 Liste simulée de spots de surf
var dataspots = []DataSpot{
	{ID: 1, Name: "Lacanau", SurfBreak: "Point Break", Photo: "https://example.com/photos/lacanau.jpg", Address: "Gironde", Difficulty: 3, SeasonStart: "2025-08-01", SeasonEnd: "2025-09-01", Rating: 0},
	{ID: 2, Name: "Hossegor", SurfBreak: "Point Break", Photo: "https://example.com/photos/hossegor.jpg", Address: "Landes", Difficulty: 4, SeasonStart: "2025-03-01", SeasonEnd: "2025-10-01", Rating: 0},
	{ID: 3, Name: "Biarritz", SurfBreak: "Point Break", Photo: "https://example.com/photos/hossegor.jpg", Address: "Landes", Difficulty: 4, SeasonStart: "2024-03-01", SeasonEnd: "2024-10-01", Rating: 0},
}

// 🌐 GET /api/spots → Tous les spots
func GetSpots(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(dataspots)
}

// 🌐 GET /api/spots/{id} → Spot par ID
func GetSpotByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id, err := strconv.Atoi(vars["id"])
	if err != nil {
		http.Error(w, "ID invalide", http.StatusBadRequest)
		return
	}

	for _, spot := range dataspots {
		if spot.ID == id {
			w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(spot)
			return
		}
	}

	http.Error(w, "Spot non trouvé", http.StatusNotFound)
}

// 🌐 PUT /api/spots/{id} → Met à jour la note d’un spot
func UpdateSpotRating(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id, err := strconv.Atoi(vars["id"])
	if err != nil {
		http.Error(w, "ID invalide", http.StatusBadRequest)
		return
	}

	var payload struct {
		Rating int `json:"rating"`
	}
	err = json.NewDecoder(r.Body).Decode(&payload)
	if err != nil {
		http.Error(w, "Corps JSON invalide", http.StatusBadRequest)
		return
	}

	for i, spot := range dataspots {
		if spot.ID == id {
			dataspots[i].Rating = payload.Rating
			w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(dataspots[i])
			return
		}
	}

	http.Error(w, "Spot non trouvé", http.StatusNotFound)
}

// 🌐 POST /api/spots → Ajoute un nouveau spot
func CreateSpot(w http.ResponseWriter, r *http.Request) {
	var newSpot DataSpot
	err := json.NewDecoder(r.Body).Decode(&newSpot)
	if err != nil {
		http.Error(w, "Données JSON invalides", http.StatusBadRequest)
		return
	}

	// Vérifie qu’un spot avec le même nom n’existe pas
	for _, spot := range dataspots {
		if spot.Name == newSpot.Name {
			http.Error(w, "Un spot avec ce nom existe déjà", http.StatusBadRequest)
			return
		}
	}

	// Auto-incrémentation de l’ID
	maxID := 0
	for _, spot := range dataspots {
		if spot.ID > maxID {
			maxID = spot.ID
		}
	}
	newSpot.ID = maxID + 1

	dataspots = append(dataspots, newSpot)

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(newSpot)
}

// 🚀 Fonction principale
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
	r := mux.NewRouter()
	r.HandleFunc("/api/spots", GetSpots).Methods("GET")
	r.HandleFunc("/api/spots/{id}", GetSpotByID).Methods("GET")
	r.HandleFunc("/api/spots/{id}", UpdateSpotRating).Methods("PUT")
	r.HandleFunc("/api/spots", CreateSpot).Methods("POST")

	// Lancement du serveur
	log.Println("Serveur en écoute sur http://localhost:8080")
	log.Fatal(http.ListenAndServe(":8080", r))
}
