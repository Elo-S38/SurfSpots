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
	ID          int    `json:"id"`          // ID unique du spot
	Name        string `json:"name"`        // Nom du spot
	SurfBreak   string `json:"surfBreak"`   // Type de vague
	Photo       string `json:"photo"`       // URL de l'image
	Address     string `json:"address"`     // Adresse ou lieu
	Difficulty  int    `json:"difficulty"`  // Niveau de difficulté (1 à 5)
	SeasonStart string `json:"seasonStart"` // Début de la meilleure période
	SeasonEnd   string `json:"seasonEnd"`   // Fin de la meilleure période
	Rating      int    `json:"rating"`      // Note du spot (modifiable via PUT)
}

// 💾 Liste simulée de spots de surf (équivalent d’une base de données en mémoire)
var dataspots = []DataSpot{
	{
		ID:          1,
		Name:        "Lacanau",
		SurfBreak:   "Point Break",
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
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(dataspots)
}

// 🌐 Route GET /api/spots/{id} → Détail d’un spot spécifique
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

// 🌐 Route PUT /api/spots/{id} → Met à jour la note (rating) d’un spot
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

// 🌐 Route POST /api/spots → Crée un nouveau spot
func CreatePost(w http.ResponseWriter, r *http.Request) {
	var newSpot DataSpot
	err := json.NewDecoder(r.Body).Decode(&newSpot)
	if err != nil {
		http.Error(w, "Corps JSON invalide", http.StatusBadRequest)
		return
	}

	// Vérifie que le nom n'existe pas déjà
	for _, spot := range dataspots {
		if spot.Name == newSpot.Name {
			http.Error(w, "Un spot avec ce nom existe déjà", http.StatusBadRequest)
			return
		}
	}

	// Génère un nouvel ID
	maxID := 0
	for _, spot := range dataspots {
		if spot.ID > maxID {
			maxID = spot.ID
		}
	}
	newSpot.ID = maxID + 1

	// Ajoute le nouveau spot
	dataspots = append(dataspots, newSpot)

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(newSpot)
}

// 🌐 POST /api/spots → Ajoute un nouveau spot à la liste
func CreateSpot(w http.ResponseWriter, r *http.Request) {
	var newSpot DataSpot

	// 📥 Decode le JSON reçu depuis le body
	err := json.NewDecoder(r.Body).Decode(&newSpot)
	if err != nil {
		http.Error(w, "Données JSON invalides", http.StatusBadRequest)
		return
	}

	// 🔢 Auto-incrémentation : on ajoute 1 au dernier ID connu
	newSpot.ID = len(dataspots) + 1

	// ➕ Ajoute le nouveau spot à la liste
	dataspots = append(dataspots, newSpot)

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated) // ✅ 201 Created
	json.NewEncoder(w).Encode(newSpot)
}


// 🚀 Fonction principale : démarre le serveur et configure les routes
func main() {
	r := mux.NewRouter()

	// 🔗 Déclare les routes disponibles et leurs méthodes HTTP
	r.HandleFunc("/api/spots", GetSpots).Methods("GET")               // 🔍 Tous les spots
	r.HandleFunc("/api/spots/{id}", GetSpotByID).Methods("GET")       // 🔍 Spot par ID
	r.HandleFunc("/api/spots/{id}", UpdateSpotRating).Methods("PUT")  // ✏️ Modifier une note
	r.HandleFunc("/api/spots", CreateSpot).Methods("POST")

	log.Println("Serveur en écoute sur http://localhost:8080")
	log.Fatal(http.ListenAndServe(":8080", r))
}
