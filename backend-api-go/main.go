package main // 📦 Point d'entrée principal du programme

import (
	"encoding/json"       // 📦 Pour encoder/décoder les données en JSON
	"log"                 // 📝 Pour afficher des messages dans le terminal
	"net/http"            // 🌐 Pour gérer les requêtes et les réponses HTTP
	"strconv"             // 🔢 Pour convertir des chaînes de caractères en entiers
	"github.com/gorilla/mux" // 🐵 Librairie externe pour gérer les routes dynamiques (ex: /spots/{id})
)

// 🎯 Définition de la structure de données envoyée à l’application mobile
type DataSpot struct {
	ID           int    `json:"id"`           // ID unique du spot
	Name         string `json:"name"`         // Nom du spot
	SurfBreak    string `json:"surfBreak"`    // Type de vague
	Photo        string `json:"photo"`        // URL de l'image
	Address      string `json:"address"`      // Adresse ou lieu
	Difficulty   int    `json:"difficulty"`   // Niveau de difficulté (1 à 5)
	SeasonStart  string `json:"seasonStart"`  // Début de la meilleure période
	SeasonEnd    string `json:"seasonEnd"`    // Fin de la meilleure période
	Rating       int    `json:"rating"`       // Note du spot (modifiable via PUT)
}

// 💾 Liste simulée de spots de surf (équivalent d’une base de données en mémoire)
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

// 🌐 Route GET /api/spots → Liste tous les spots
func GetSpots(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")   // 🏷 Spécifie que la réponse est en JSON
	json.NewEncoder(w).Encode(dataspots)                 // 📤 Encode la liste en JSON et l’envoie
}

// 🌐 Route GET /api/spots/{id} → Détail d’un spot spécifique
func GetSpotByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)                    // 🔍 Récupère les variables de l’URL
	idParam := vars["id"]                  // 🧾 Extrait la valeur de l’ID (en string)
	id, err := strconv.Atoi(idParam)      // 🔄 Convertit la string en entier
	if err != nil {
		http.Error(w, "ID invalide", http.StatusBadRequest) // ❌ Si erreur de conversion → 400
		return
	}

	for _, spot := range dataspots {      // 🔁 Parcourt chaque spot de la liste
		if spot.ID == id {                // ✅ Si l’ID correspond
			w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(spot) // 📤 Envoie le spot correspondant
			return
		}
	}

	http.Error(w, "Spot non trouvé", http.StatusNotFound) // ❌ Aucun spot trouvé → 404
}

// 🌐 Route PUT /api/spots/{id} → Met à jour la note (rating) d’un spot
func UpdateSpotRating(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)                         // 🔍 Récupère les variables de l’URL
	idParam := vars["id"]
	id, err := strconv.Atoi(idParam)           // 🔄 Convertit l’ID en entier
	if err != nil {
		http.Error(w, "ID invalide", http.StatusBadRequest) // ❌ ID non numérique
		return
	}

	// 📥 Structure temporaire pour recevoir la nouvelle note
	var payload struct {
		Rating int `json:"rating"`              // Le champ attendu dans le JSON reçu
	}
	err = json.NewDecoder(r.Body).Decode(&payload) // 📩 Decode le corps JSON reçu
	if err != nil {
		http.Error(w, "Corps JSON invalide", http.StatusBadRequest) // ❌ JSON mal formé
		return
	}

	for i, spot := range dataspots {              // 🔁 Recherche du spot à mettre à jour
		if spot.ID == id {
			dataspots[i].Rating = payload.Rating  // ✅ Met à jour la note
			w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(dataspots[i]) // 📤 Renvoie le spot mis à jour
			return
		}
	}

	http.Error(w, "Spot non trouvé", http.StatusNotFound) // ❌ ID inexistant → 404
}

// 🚀 Fonction principale : démarre le serveur et configure les routes
func main() {
	r := mux.NewRouter() // 🧭 Initialise le routeur Gorilla Mux

	// 🔗 Déclare les routes disponibles et leurs méthodes HTTP
	r.HandleFunc("/api/spots", GetSpots).Methods("GET")               // 🔍 Tous les spots
	r.HandleFunc("/api/spots/{id}", GetSpotByID).Methods("GET")       // 🔍 Spot par ID
	r.HandleFunc("/api/spots/{id}", UpdateSpotRating).Methods("PUT")  // ✏️ Modifier une note

	log.Println("Serveur en écoute sur http://localhost:8080")        // 🟢 Message de démarrage
	log.Fatal(http.ListenAndServe(":8080", r))                        // 🚀 Lance le serveur
}
