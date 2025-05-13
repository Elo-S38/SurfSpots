package main

import (
	"encoding/json" // 📦 Pour convertir des objets Go en JSON
	"log"           // 📝 Pour écrire dans la console
	"net/http"      // 🌐 Pour créer le serveur web
	"strconv"       // 🔢 Pour convertir des strings en int
	"github.com/gorilla/mux" // 🐵 Pour gérer les routes dynamiques comme /spots/{id}
)

// 🎯 Structure d’un Spot (envoyé à l’application mobile)
type DataSpot struct {
	ID           int    `json:"id"`
	Name         string `json:"name"`
	SurfBreak    string `json:"surfBreak"`
	Photo        string `json:"photo"`
	Address      string `json:"address"`
	Difficulty   int    `json:"difficulty"`
	SeasonStart  string `json:"seasonStart"`
	SeasonEnd    string `json:"seasonEnd"`
	Rating       int    `json:"rating"` // ✅ Ajout du champ modifiable
}

// 💾 Simili base de données (en mémoire)
var dataspots = []DataSpot{
	{
		ID:          1,
		Name:        "Lacanau",
		SurfBreak:   "Beach Break",
		Photo:       "https://example.com/photos/lacanau.jpg",
		Address:     "Gironde",
		Difficulty:  3,
		SeasonStart: "2025-08-01",
		SeasonEnd:   "2025-09-01",
		Rating:      0,
	},
	{
		ID:          2,
		Name:        "Hossegor",
		SurfBreak:   "Point Break",
		Photo:       "https://example.com/photos/hossegor.jpg",
		Address:     "Landes",
		Difficulty:  4,
		SeasonStart: "2025-03-01",
		SeasonEnd:   "2025-10-01",
		Rating:      0,
	},
	{
		ID:          3,
		Name:        "Biarritz",
		SurfBreak:   "Point Break",
		Photo:       "https://example.com/photos/hossegor.jpg",
		Address:     "Landes",
		Difficulty:  4,
		SeasonStart: "2024-03-01",
		SeasonEnd:   "2024-10-01",
		Rating:      0,
	},
}

// 🌐 GET /api/spots → Liste des spots
func GetSpots(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(dataspots)
}

// 🌐 GET /api/spots/{id} → Détail d’un spot
func GetSpotByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	idParam := vars["id"]
	id, err := strconv.Atoi(idParam)
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
	idParam := vars["id"]
	id, err := strconv.Atoi(idParam)
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

// 🚀 main → Lance le serveur et configure les routes
func main() {
	r := mux.NewRouter()

	r.HandleFunc("/api/spots", GetSpots).Methods("GET")
	r.HandleFunc("/api/spots/{id}", GetSpotByID).Methods("GET")
	r.HandleFunc("/api/spots/{id}", UpdateSpotRating).Methods("PUT")

	log.Println("Serveur en écoute sur http://localhost:8080")
	log.Fatal(http.ListenAndServe(":8080", r))
}
