const API_BASE = "/api/ratings";
const titleDetailsCache = {};

const searchInput = document.getElementById("searchInput");
const searchButton = document.getElementById("searchButton");
const resultsContainer = document.getElementById("results");
const ratingsContainer = document.getElementById("ratings");

let currentType = "movie";
let ratings = {};

searchButton.addEventListener("click", handleSearch);
searchInput.addEventListener("keydown", (event) => {
  if (event.key === "Enter") {
    handleSearch();
  }
});

document.querySelectorAll("input[name=searchType]").forEach((input) => {
  input.addEventListener("change", () => {
    currentType = input.value;
    searchInput.placeholder = {
      movie: "Filmtitel eingeben...",
      tv: "Serientitel eingeben...",
      anime: "Anime-Titel eingeben...",
    }[currentType];
  });
});

window.addEventListener("load", async () => {
  await loadRatings();
  renderRatings();
});

async function handleSearch() {
  const query = searchInput.value.trim();
  if (!query) {
    resultsContainer.innerHTML =
      '<div class="no-results">Bitte gib einen Suchbegriff ein.</div>';
    return;
  }

  resultsContainer.innerHTML = '<div class="no-results">Suche...</div>';

  try {
    const items = await searchItems(currentType, query);
    renderResults(items);
  } catch (error) {
    console.error(error);
    resultsContainer.innerHTML = `
      <div class="no-results">
        Fehler bei der Suche. ${error.message || "Überprüfe die Konsole."}
      </div>`;
  }
}

async function searchItems(type, query) {
  const response = await fetch(
    `/api/search?type=${encodeURIComponent(type)}&query=${encodeURIComponent(query)}`,
  );
  if (!response.ok) {
    throw new Error("Fehler bei der Suche");
  }

  const data = await response.json();
  return data.map((entry) => ({
    id: entry.id,
    type: entry.type,
    title: entry.title,
    year: entry.year || "n/a",
    subtitle: entry.subtitle || entry.type || "",
    overview: entry.overview || "Keine Beschreibung verfügbar.",
    poster: entry.poster || "",
    imdbRating: entry.imdbRating || null,
    score: entry.score || null,
    runtimeSeconds: entry.runtimeSeconds || null,
    genres: entry.genres || [],
    plotFetched: Boolean(entry.plotFetched),
  }));
}

async function loadTitleDetails(id) {
  if (titleDetailsCache[id]) {
    return titleDetailsCache[id];
  }

  const response = await fetch(
    `/api/title-details?id=${encodeURIComponent(id)}`,
  );
  if (!response.ok) {
    throw new Error("Fehler beim Laden der Details");
  }

  const data = await response.json();
  titleDetailsCache[id] = data;
  return data;
}

async function hydratePlot(card, item) {
  if (item.type === "anime" || item.plotFetched || !item.id) {
    return;
  }

  try {
    const details = await loadTitleDetails(item.id);
    item.overview = details.plot || item.overview;
    item.poster = item.poster || details.primaryImage?.url || item.poster;
    item.imdbRating = details.rating?.aggregateRating || item.imdbRating;
    item.runtimeSeconds = details.runtimeSeconds || item.runtimeSeconds;
    item.genres = details.genres || item.genres;
    item.plotFetched = true;

    const description = card.querySelector(".card-description");
    if (description) {
      description.textContent = item.overview;
    }

    const poster = card.querySelector("img");
    if (poster && item.poster) {
      poster.src = item.poster;
    }

    const detailsLine = card.querySelector(".card-details");
    if (detailsLine) {
      detailsLine.textContent = buildInfoLine(item);
    }
  } catch (error) {
    console.warn("IMDb Detail-Laden fehlgeschlagen", error);
  }
}

function renderResults(items) {
  if (!items.length) {
    resultsContainer.innerHTML =
      '<div class="no-results">Keine Ergebnisse gefunden.</div>';
    return;
  }

  resultsContainer.innerHTML = "";
  items.forEach((item) => {
    const card = document.createElement("article");
    card.className = "card";

    const poster = document.createElement("img");
    poster.src =
      item.poster || "https://via.placeholder.com/92x138?text=Kein+Bild";
    poster.alt = `${item.title} Poster`;

    const content = document.createElement("div");
    content.className = "card-content";

    const title = document.createElement("h3");
    title.className = "card-title";
    title.textContent = `${item.title} (${item.year})`;

    const meta = document.createElement("div");
    meta.className = "card-meta";
    meta.textContent = `${item.subtitle} • ${capitalize(item.type)}`;

    const detailsLine = document.createElement("div");
    detailsLine.className = "card-details";
    detailsLine.textContent = buildInfoLine(item);

    const description = document.createElement("p");
    description.className = "card-description";
    description.textContent = item.overview;

    const stars = createStarRow(item);

    content.append(title, meta, detailsLine, description, stars);
    card.append(poster, content);
    resultsContainer.appendChild(card);
    hydratePlot(card, item);
  });
}

function createStarRow(item) {
  const row = document.createElement("div");
  row.className = "star-row";

  const ratingValue = getRatingValue(item);
  const label = document.createElement("div");
  label.className = "rating-label";
  label.textContent = ratingValue
    ? `Bewertung: ${ratingValue} Stern${ratingValue === 1 ? "" : "e"}`
    : "Noch nicht bewertet";
  label.style.marginRight = "10px";
  label.style.alignSelf = "center";

  row.append(label);

  for (let i = 1; i <= 5; i += 1) {
    const star = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    star.setAttribute("viewBox", "0 0 24 24");
    star.classList.add("star");
    if (i <= ratingValue) star.classList.add("filled");
    star.innerHTML = `
      <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
    `;
    star.addEventListener("click", async () => {
      await saveRating(item, i);
      await loadRatings();
      renderRatings();
      renderResults(await searchItems(currentType, searchInput.value.trim()));
    });
    row.append(star);
  }

  return row;
}

function getRatingValue(item) {
  const key = buildRatingKey(item);
  return ratings[key]?.score || 0;
}

function buildInfoLine(item) {
  const parts = [];
  if (item.imdbRating) parts.push(`IMDb ${item.imdbRating}`);
  if (item.score && item.type === "anime") parts.push(`Score ${item.score}`);
  if (item.runtimeSeconds) parts.push(formatRuntime(item.runtimeSeconds));
  const genres = normalizeGenres(item.genres);
  if (genres) parts.push(genres);
  return parts.join(" • ");
}

function normalizeGenres(genres) {
  if (!genres) return "";
  if (Array.isArray(genres)) {
    return genres.join(", ");
  }
  return String(genres);
}

function formatRuntime(seconds) {
  const minutes = Math.round(seconds / 60);
  if (minutes >= 60) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return mins ? `${hours}h ${mins}m` : `${hours}h`;
  }
  return `${minutes}m`;
}

function buildRatingKey(item) {
  return `${item.type}-${item.id}`;
}

async function saveRating(item, value) {
  const payload = {
    externalId: item.id,
    type: item.type,
    title: item.title,
    year: item.year,
    score: value,
    overview: item.overview,
    poster: item.poster,
    genres: item.genres?.join(", ") || "",
  };

  const response = await fetch(API_BASE, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error("Fehler beim Speichern der Bewertung");
  }
}

async function loadRatings() {
  try {
    const response = await fetch(API_BASE);
    if (!response.ok) {
      throw new Error("API konnte nicht geladen werden");
    }
    const data = await response.json();
    ratings = data.reduce((map, item) => {
      map[`${item.type}-${item.externalId}`] = item;
      return map;
    }, {});
  } catch (error) {
    console.error(error);
    ratings = {};
  }
}

function renderRatings() {
  const entries = Object.values(ratings).sort(
    (a, b) => new Date(b.ratedAt) - new Date(a.ratedAt),
  );
  ratingsContainer.innerHTML = "";

  if (!entries.length) {
    ratingsContainer.innerHTML =
      '<div class="no-results">Noch keine Bewertungen vorhanden.</div>';
    return;
  }

  entries.forEach((item) => {
    const card = document.createElement("article");
    card.className = "card";

    const poster = document.createElement("img");
    poster.src =
      item.poster || "https://via.placeholder.com/92x138?text=Kein+Bild";
    poster.alt = `${item.title} Poster`;

    const content = document.createElement("div");
    content.className = "card-content";

    const title = document.createElement("h3");
    title.className = "card-title";
    title.textContent = `${item.title} (${item.year})`;

    const meta = document.createElement("div");
    meta.className = "card-meta";
    meta.textContent = `${item.type === "tv" ? "Serie" : item.type === "anime" ? "Anime" : "Film"}`;

    const detailsLine = document.createElement("div");
    detailsLine.className = "card-details";
    detailsLine.textContent = buildInfoLine(item);

    const stars = document.createElement("div");
    stars.className = "star-row";
    for (let i = 1; i <= 5; i += 1) {
      const star = document.createElementNS(
        "http://www.w3.org/2000/svg",
        "svg",
      );
      star.setAttribute("viewBox", "0 0 24 24");
      star.classList.add("star");
      if (i <= item.score) star.classList.add("filled");
      star.innerHTML = `
        <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
      `;
      stars.appendChild(star);
    }

    content.append(title, meta, detailsLine, stars);
    card.append(poster, content);
    ratingsContainer.appendChild(card);
  });
}

function capitalize(value) {
  if (value === "tv") return "Serie";
  if (value === "anime") return "Anime";
  if (value === "movie") return "Film";
  return value.charAt(0).toUpperCase() + value.slice(1);
}
