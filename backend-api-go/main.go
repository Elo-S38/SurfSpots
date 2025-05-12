package main

import (
	"encoding/json"
	"log"
	"net/http"
	"os"
	"github.com/gorilla/mux"
)

// Structure de la réponse (on garde tout brut pour l'étape 1)
type AirtableResponse struct {
	Records []map[string]interface{} `json:"records"`
}

func main() {
	r := mux.NewRouter()

	// Route GET /api/spots
	r.HandleFunc("/api/spots", getSpots).Methods("GET")

	log.Println("✅ Serveur lancé sur http://localhost:8080")
	err := http.ListenAndServe(":8080", r)
	if err != nil {
		log.Fatalf("❌ Erreur serveur : %v", err)
	}
}

func getSpots(w http.ResponseWriter, r *http.Request) {
	// 🔍 Ouvre le fichier JSON dans le dossier data/
	file, err := os.ReadFile("data/spots.json")
	if err != nil {
		log.Printf("❌ Erreur d'ouverture : %v", err)
		http.Error(w, "Erreur d'ouverture du fichier JSON", http.StatusInternalServerError)
		return
	}

	var result AirtableResponse
	err = json.Unmarshal(file, &result)
	if err != nil {
		log.Printf("❌ Erreur de parsing JSON : %v", err)
		http.Error(w, "Format JSON invalide", http.StatusInternalServerError)
		return
	}

	// ✅ Réponse en JSON
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(result.Records)
}
