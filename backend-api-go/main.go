package main

import (
	"encoding/json"       // 📦 Pour convertir des objets Go en JSON
	"log"                 // 📝 Pour écrire dans la console
	"net/http"            // 🌐 Pour créer le serveur web
	"strconv"             // 🔢 Pour convertir des strings en int
	"github.com/gorilla/mux" // 🐵 Pour gérer les routes dynamiques comme /spots/{id}
)

// 🎯 Définition de la structure d’un Spot (correspond au format JSON que verra l'app mobile)
type DataSpot struct {
	ID           int    `json:"id"`
	Name         string `json:"name"`
	SurfBreak    string `json:"surfBreak"`
	Photo        string `json:"photo"`
	Address      string `json:"address"`
	Difficulty   int    `json:"difficulty"`
	SeasonStart  string `json:"seasonStart"`
	SeasonEnd    string `json:"seasonEnd"`
}


// 💾 Liste simulée de spots (comme une base de données mais en mémoire)
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
	},
}


// 🌐 Route GET /api/spots → renvoie la liste complète des spots
func GetSpots(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json") // 📝 Indique qu’on va envoyer du JSON
	json.NewEncoder(w).Encode(dataspots)                   // 📤 Convertit `spots` en JSON et l’envoie
}

// 🌐 Route GET /api/spots/{id} → renvoie les détails d’un seul spot
func GetSpotByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)        // 🔍 Récupère les paramètres dans l’URL (ex: {id})
	idParam := vars["id"]      // 🧾 Extrait la valeur de {id} en string

	id, err := strconv.Atoi(idParam) // 🔁 Convertit la string "2" en entier 2
	if err != nil {
		http.Error(w, "ID invalide", http.StatusBadRequest) // 🔴 Erreur 400 si ce n’est pas un nombre
		return
	}

	// 🔎 Cherche dans la liste si un spot correspond à cet ID
	for _, spot := range dataspots {
		if spot.ID == id {
			w.Header().Set("Content-Type", "application/json") // 📤 JSON en sortie
			json.NewEncoder(w).Encode(spot)                    // ✅ Spot trouvé, on l’envoie
			return
		}
	}

	// ❌ Aucun spot trouvé → on envoie une erreur 404
	http.Error(w, "Spot non trouvé", http.StatusNotFound)
}

// 🚀 Fonction principale : démarre le serveur web et configure les routes
func main() {
	r := mux.NewRouter() // 🧭 Initialise le routeur Gorilla Mux

	// 📌 Route pour la liste des spots
	r.HandleFunc("/api/spots", GetSpots).Methods("GET")

	// 📌 Route pour un spot spécifique avec un paramètre {id}
	r.HandleFunc("/api/spots/{id}", GetSpotByID).Methods("GET")

	log.Println("Serveur en écoute sur http://localhost:8080") // ✅ Message de démarrage
	log.Fatal(http.ListenAndServe(":8080", r))                 // 🟢 Démarre le serveur sur le port 8080
}
