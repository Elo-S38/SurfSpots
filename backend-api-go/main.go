package main

import (
    "encoding/json"
    "log"
    "net/http"

    "github.com/gorilla/mux"
)

// 🎯 Struct qui décrit un spot à renvoyer à l'application mobile
type Spot struct {
		Name       string `json:"name"` // Nom du spot
    SurfBreak string `json:"surfBreak"` // Type de vague (ex: Beach Break)
    Photo     string `json:"photo"`     // URL de l'image ou nom de fichier local
    Address   string `json:"address"`   // Localisation du spot
}

// 💾 Données simulées en mémoire (pas de base de données pour l’instant)
var spots = []Spot{
    {		
				Name:      "Lacanau",
        SurfBreak: "Beach Break",
        Photo:     "https://example.com/photos/lacanau.jpg",
        Address:   "Lacanau, Gironde",
    },
    {
				Name:      "Hossegor",
        SurfBreak: "Point Break",
        Photo:     "https://example.com/photos/hossegor.jpg",
        Address:   "Hossegor, Landes",
    },
    {
				Name:      "Biarritz",
        SurfBreak: "Reef Break",
        Photo:     "https://example.com/photos/biarritz.jpg",
        Address:   "Biarritz, Pays basque",
    },
}

// 🌐 Route GET /api/spots
func GetSpots(w http.ResponseWriter, r *http.Request) {
    w.Header().Set("Content-Type", "application/json") // Réponse en JSON
    json.NewEncoder(w).Encode(spots) // On renvoie la liste simulée
}

func main() {
    r := mux.NewRouter()                           // 🧭 Initialisation du routeur
    r.HandleFunc("/api/spots", GetSpots).Methods("GET") // Définition de la route GET

    log.Println("Serveur en écoute sur http://localhost:8080")
    log.Fatal(http.ListenAndServe(":8080", r))     // 🚀 Lancement du serveur web
}
