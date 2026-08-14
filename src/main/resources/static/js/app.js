// App State
let allProducts = [];
let allIngredients = [];
let selectedProducts = [];

// Base API URL (Relative to host since it's served on the same port)
const API_BASE = "";

// -------------------------------------------------------------
// 1. Core SPA Navigation
// -------------------------------------------------------------
function switchTab(tabId) {
    // Hide all tab panels
    document.querySelectorAll('.tab-panel').forEach(panel => {
        panel.classList.remove('active');
    });
    
    // Remove active state from all nav buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    // Show selected panel
    const targetPanel = document.getElementById(`${tabId}-tab`);
    if (targetPanel) {
        targetPanel.classList.add('active');
    }

    // Set clicked button to active
    // We find the button that has the onclick containing the tabId
    const buttons = document.querySelectorAll('.tab-btn');
    buttons.forEach(btn => {
        if (btn.getAttribute('onclick').includes(tabId)) {
            btn.classList.add('active');
        }
    });

    // Trigger tab-specific loads
    if (tabId === 'routine' && allProducts.length === 0) {
        loadCatalog();
    } else if (tabId === 'analyzer' && allIngredients.length === 0) {
        loadIngredientsForAnalyzer();
    } else if (tabId === 'intelligence') {
        loadGraphIntelligence();
    }
}

// Helper to generate cosmetic colored circles for product placeholder images
function getProductAvatarSvg(productName, category) {
    const firstChar = productName.charAt(0).toUpperCase();
    // Choose cream/cosmetic colors based on name hash
    let hash = 0;
    for (let i = 0; i < productName.length; i++) {
        hash = productName.charCodeAt(i) + ((hash << 5) - hash);
    }
    const colors = [
        { bg: '#EADCD0', text: '#5D4037' }, // warm beige
        { bg: '#DCE7E1', text: '#2E4C3E' }, // sage green
        { bg: '#E5D4CD', text: '#6D4C41' }, // terracotta blush
        { bg: '#D1E3E7', text: '#1F4E5B' }, // pale ocean
        { bg: '#ECD8E2', text: '#6D3F5B' }  // rose quartz
    ];
    const index = Math.abs(hash % colors.length);
    const color = colors[index];

    return `
    <svg viewBox="0 0 100 100" class="placeholder-avatar">
        <circle cx="50" cy="50" r="48" fill="${color.bg}" />
        <text x="50%" y="54%" dominant-baseline="middle" text-anchor="middle" font-family="'Playfair Display', serif" font-size="32" fill="${color.text}">${firstChar}</text>
    </svg>
    `;
}

// -------------------------------------------------------------
// 2. Concern Recommendations Tab
// -------------------------------------------------------------
async function getRecommendations() {
    const concern = document.getElementById("concern-select").value;
    const grid = document.getElementById("rec-products");
    const msg = document.getElementById("rec-message");
    const loader = document.getElementById("rec-loading");

    grid.innerHTML = "";
    msg.classList.add("hidden");
    msg.textContent = "";

    if (!concern) {
        return;
    }

    try {
        loader.classList.remove("hidden");
        const response = await fetch(`${API_BASE}/api/recommendations?concern=${encodeURIComponent(concern)}`);
        const data = await response.json();

        loader.classList.add("hidden");

        if (!response.ok) {
            throw new Error(data.message || "Failed to fetch recommendations");
        }

        if (data.error || (data.message && data.message.includes("No products"))) {
            msg.textContent = data.message || data.error;
            msg.className = "info-alert warn-alert";
            msg.classList.remove("hidden");
            return;
        }

        // Render recommendations
        data.forEach(product => {
            const card = document.createElement("div");
            card.className = "product-card";
            
            // Use actual image if available, fallback to dynamic avatar
            const mediaHtml = product.imageUrl 
                ? `<img src="images/${product.imageUrl}" alt="${product.productName}" class="product-thumbnail" onerror="this.outerHTML=getProductAvatarSvg('${product.productName.replace(/'/g, "\\'")}', '${product.category}')">`
                : getProductAvatarSvg(product.productName, product.category);

            card.innerHTML = `
                <div class="product-avatar-wrapper">${mediaHtml}</div>
                <div class="product-info-wrapper">
                    <span class="category-pill">${product.category}</span>
                    <h3 class="product-title">${product.productName}</h3>
                    <p class="brand-text">by ${product.brand}</p>
                    <p class="ingredient-highlights"><strong>Actives:</strong> ${product.ingredient}</p>
                    <div class="product-footer">
                        <span class="price-text">₹${product.price}</span>
                    </div>
                </div>
            `;
            grid.appendChild(card);
        });

    } catch (err) {
        loader.classList.add("hidden");
        msg.textContent = `Error: ${err.message}`;
        msg.className = "info-alert error-alert";
        msg.classList.remove("hidden");
    }
}

// -------------------------------------------------------------
// 3. Routine Checker Tab
// -------------------------------------------------------------
async function loadCatalog() {
    const listContainer = document.getElementById("catalog-list");
    const loader = document.getElementById("catalog-loading");

    listContainer.innerHTML = "";
    loader.classList.remove("hidden");

    try {
        const response = await fetch(`${API_BASE}/api/products`);
        if (!response.ok) throw new Error("Failed to load catalog");
        
        allProducts = await response.json();
        loader.classList.add("hidden");

        allProducts.forEach(product => {
            const item = document.createElement("div");
            item.className = "catalog-item";
            item.innerHTML = `
                <label class="checkbox-container">
                    <input type="checkbox" value="${product.name}" onchange="toggleProductSelection(this)">
                    <span class="checkmark"></span>
                    <div class="item-details">
                        <span class="item-name">${product.name}</span>
                        <span class="item-meta">${product.brand} &bull; ${product.category}</span>
                    </div>
                </label>
            `;
            listContainer.appendChild(item);
        });

    } catch (err) {
        loader.classList.add("hidden");
        listContainer.innerHTML = `<p class="error-text">Failed to load product catalog: ${err.message}</p>`;
    }
}

function toggleProductSelection(checkbox) {
    const name = checkbox.value;
    if (checkbox.checked) {
        if (!selectedProducts.includes(name)) {
            selectedProducts.push(name);
        }
    } else {
        selectedProducts = selectedProducts.filter(item => item !== name);
    }

    updateSelectedRoutineUI();
}

function updateSelectedRoutineUI() {
    const container = document.getElementById("selected-routine-pills");
    const btn = document.getElementById("check-routine-btn");
    container.innerHTML = "";

    if (selectedProducts.length === 0) {
        container.innerHTML = `<span class="empty-routine-text">No products added. Select products from the catalog to build your routine.</span>`;
        btn.disabled = true;
        return;
    }

    selectedProducts.forEach(name => {
        const pill = document.createElement("span");
        pill.className = "routine-pill";
        pill.innerHTML = `
            ${name}
            <span class="remove-pill" onclick="removeProduct('${name}')">&times;</span>
        `;
        container.appendChild(pill);
    });

    // Check routine button enabled only if at least 2 products are selected
    btn.disabled = selectedProducts.length < 2;
}

function removeProduct(name) {
    // Uncheck in checklist
    const checkbox = document.querySelector(`.catalog-item input[value="${name}"]`);
    if (checkbox) {
        checkbox.checked = false;
    }
    selectedProducts = selectedProducts.filter(item => item !== name);
    updateSelectedRoutineUI();
}

function clearRoutine() {
    // Uncheck all inputs
    document.querySelectorAll('.catalog-item input').forEach(input => {
        input.checked = false;
    });
    selectedProducts = [];
    updateSelectedRoutineUI();
    
    // Reset output
    document.getElementById("analysis-output").classList.add("hidden");
}

async function checkRoutineConflicts() {
    const loader = document.getElementById("routine-loading");
    const output = document.getElementById("analysis-output");
    const statusBox = document.getElementById("routine-status-box");
    const statusIcon = document.getElementById("routine-status-icon");
    const statusTitle = document.getElementById("routine-status-title");
    const statusDesc = document.getElementById("routine-status-desc");
    const conflictsSection = document.getElementById("routine-conflicts-section");
    const conflictsList = document.getElementById("routine-conflicts-list");

    output.classList.add("hidden");
    conflictsSection.classList.add("hidden");
    conflictsList.innerHTML = "";

    if (selectedProducts.length < 2) {
        return;
    }

    try {
        loader.classList.remove("hidden");

        const response = await fetch(`${API_BASE}/api/routine/conflicts`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(selectedProducts)
        });

        const data = await response.json();
        loader.classList.add("hidden");
        output.classList.remove("hidden");

        // If response is clean (no conflicts)
        if (response.ok && (data.message && data.message.includes("No conflicts"))) {
            statusBox.className = "status-summary-box safe-box";
            statusIcon.textContent = "💚";
            statusTitle.textContent = "Routine is Safe!";
            statusDesc.textContent = "CognoDB checked all layers. No active ingredient conflicts found between the selected products.";
            return;
        }

        if (!response.ok) {
            throw new Error(data.message || "Conflict analysis failed");
        }

        // If conflicts found
        statusBox.className = "status-summary-box conflict-box";
        statusIcon.textContent = "🚨";
        statusTitle.textContent = "Layering Risks Detected!";
        statusDesc.textContent = `${data.length} potential conflict relationship(s) mapped in your routine layers. Review details below.`;

        conflictsSection.classList.remove("hidden");
        data.forEach(conflict => {
            const item = document.createElement("div");
            item.className = "report-item";
            item.innerHTML = `
                <div class="report-header">
                    <span class="risk-badge badge-${conflict.severity}">${conflict.severity.toUpperCase()} RISK</span>
                    <span class="report-products">${conflict.product1} &bull; ${conflict.product2}</span>
                </div>
                <div class="report-body">
                    <p class="report-ingredient-conflict">
                        <strong>Active Conflict:</strong> 
                        <span class="accent-text">${conflict.ingredient1}</span> conflicts with <span class="accent-text">${conflict.ingredient2}</span>
                    </p>
                    <p class="report-reason"><strong>Reason:</strong> ${conflict.reason}</p>
                </div>
            `;
            conflictsList.appendChild(item);
        });

    } catch (err) {
        loader.classList.add("hidden");
        output.classList.remove("hidden");
        statusBox.className = "status-summary-box error-box";
        statusIcon.textContent = "❌";
        statusTitle.textContent = "Analysis Failed";
        statusDesc.textContent = err.message;
    }
}

// -------------------------------------------------------------
// 4. Pair Analyzer Tab
// -------------------------------------------------------------
async function loadIngredientsForAnalyzer() {
    const sel1 = document.getElementById("ingredient1");
    const sel2 = document.getElementById("ingredient2");
    
    sel1.innerHTML = '<option value="">-- Select Active Ingredient --</option>';
    sel2.innerHTML = '<option value="">-- Select Active Ingredient --</option>';

    try {
        const response = await fetch(`${API_BASE}/api/products`);
        if (!response.ok) throw new Error();
        const products = await response.json();

        // Fetch ingredients from products dynamically or use a known set
        // SeedDataRunner lists: Retinol, Vitamin C, Niacinamide, Hyaluronic Acid, Salicylic Acid, Benzoyl Peroxide, Glycolic Acid, Vitamin E, Ceramides, Zinc Oxide, Azelaic Acid, Squalane
        const ingredients = [
            "Retinol", "Vitamin C", "Niacinamide", "Hyaluronic Acid", 
            "Salicylic Acid", "Benzoyl Peroxide", "Glycolic Acid", 
            "Vitamin E", "Ceramides", "Zinc Oxide", "Azelaic Acid", "Squalane"
        ];
        allIngredients = ingredients.sort();

        allIngredients.forEach(ing => {
            const opt1 = document.createElement("option");
            opt1.value = ing;
            opt1.textContent = ing;
            sel1.appendChild(opt1);

            const opt2 = document.createElement("option");
            opt2.value = ing;
            opt2.textContent = ing;
            sel2.appendChild(opt2);
        });

    } catch (err) {
        // Fallback static list
        const ingredients = ["Retinol", "Vitamin C", "Niacinamide", "Hyaluronic Acid", "Salicylic Acid", "Benzoyl Peroxide"];
        ingredients.forEach(ing => {
            const opt = new Option(ing, ing);
            sel1.add(opt.cloneNode(true));
            sel2.add(opt);
        });
    }
}

async function analyzePair() {
    const ing1 = document.getElementById("ingredient1").value;
    const ing2 = document.getElementById("ingredient2").value;
    const loader = document.getElementById("pair-loading");
    const results = document.getElementById("pair-results");
    const heading = document.getElementById("pair-heading");
    const badge = document.getElementById("pair-badge");
    const desc = document.getElementById("pair-description");
    const card = document.getElementById("pair-card");

    results.classList.add("hidden");

    if (!ing1 || !ing2) {
        alert("Please select both ingredients.");
        return;
    }

    if (ing1 === ing2) {
        alert("Please select two different ingredients.");
        return;
    }

    try {
        loader.classList.remove("hidden");

        // 1. Query Conflicts
        const conflictRes = await fetch(`${API_BASE}/api/conflicts/reason?ingredient1=${encodeURIComponent(ing1)}&ingredient2=${encodeURIComponent(ing2)}`);
        const conflictData = await conflictRes.json();

        // 2. Query Synergies
        const synergyRes = await fetch(`${API_BASE}/api/synergies?ingredient1=${encodeURIComponent(ing1)}&ingredient2=${encodeURIComponent(ing2)}`);
        const synergyData = await synergyRes.json();

        loader.classList.add("hidden");
        results.classList.remove("hidden");

        heading.textContent = `${ing1} + ${ing2}`;

        // Case A: Conflict exists
        if (conflictRes.ok && Array.isArray(conflictData) && conflictData.length > 0) {
            const c = conflictData[0];
            card.className = "pair-result-card border-red";
            badge.className = "badge badge-high";
            badge.textContent = `CONFLICT: ${c.severity.toUpperCase()}`;
            desc.innerHTML = `<strong>Relationship Check:</strong> Layering these causes problems. <br><br><strong>Details:</strong> ${c.reason}`;
            return;
        }

        // Case B: Synergy exists
        if (synergyRes.ok && Array.isArray(synergyData) && synergyData.length > 0) {
            const s = synergyData[0];
            card.className = "pair-result-card border-green";
            badge.className = "badge badge-synergy";
            badge.textContent = "SYNERGY";
            desc.innerHTML = `<strong>Relationship Check:</strong> Perfect pairing! <br><br><strong>Details:</strong> ${s.benefit}`;
            return;
        }

        // Case C: Neutral relationship
        card.className = "pair-result-card border-neutral";
        badge.className = "badge badge-neutral";
        badge.textContent = "NEUTRAL";
        desc.innerHTML = `<strong>Relationship Check:</strong> No specific synergy or conflict relationship is mapped in the graph database. They are generally safe to layer if tolerated by your skin.`;

    } catch (err) {
        loader.classList.add("hidden");
        alert("Lookup failed: " + err.message);
    }
}

// -------------------------------------------------------------
// 5. Graph Intelligence Tab
// -------------------------------------------------------------
async function loadGraphIntelligence() {
    const probLoader = document.getElementById("intel-problematic-loading");
    const riskLoader = document.getElementById("intel-risks-loading");
    const probList = document.getElementById("intel-problematic-list");
    const riskList = document.getElementById("intel-risks-list");

    probList.innerHTML = "";
    riskList.innerHTML = "";

    probLoader.classList.remove("hidden");
    riskLoader.classList.remove("hidden");

    // A. Fetch problematic ranking
    try {
        const response = await fetch(`${API_BASE}/api/ingredients/problematic`);
        const data = await response.json();
        probLoader.classList.add("hidden");

        if (data.length === 0) {
            probList.innerHTML = '<p class="info-text text-center">No rankings found.</p>';
        } else {
            data.forEach((item, index) => {
                const row = document.createElement("div");
                row.className = "ranking-row clickable-row";
                row.setAttribute("title", "Click to view conflict details");
                row.onclick = () => showIngredientConflictDetails(item.ingredient);
                row.innerHTML = `
                    <div class="ranking-rank">#${index + 1}</div>
                    <div class="ranking-name">${item.ingredient}</div>
                    <div class="ranking-count badge-high">${item.conflictCount} conflicts</div>
                `;
                probList.appendChild(row);
            });
        }
    } catch (err) {
        probLoader.classList.add("hidden");
        probList.innerHTML = `<p class="error-text">Failed to load rankings: ${err.message}</p>`;
    }

    // B. Fetch indirect risks (2-3 hops)
    try {
        const response = await fetch(`${API_BASE}/api/risks/indirect`);
        const data = await response.json();
        riskLoader.classList.add("hidden");

        if (data.length === 0) {
            riskList.innerHTML = '<p class="info-text text-center">No indirect risk paths found in dataset.</p>';
        } else {
            // Only show top 10 to keep it clean
            const itemsToShow = data.slice(0, 10);
            itemsToShow.forEach(item => {
                const row = document.createElement("div");
                row.className = "path-row";
                
                // Form path arrows visual
                const pathVisual = item.conflictPath.join(" &rarr; ");

                row.innerHTML = `
                    <div class="path-title">
                        <strong>${item.sourceIngredient}</strong> triggers indirect risk with <strong>${item.targetIngredient}</strong>
                        <span class="path-hops-badge">${item.pathLength} hops</span>
                    </div>
                    <div class="path-visual">${pathVisual}</div>
                `;
                riskList.appendChild(row);
            });
        }
    } catch (err) {
        riskLoader.classList.add("hidden");
        riskList.innerHTML = `<p class="error-text">Failed to load risk paths: ${err.message}</p>`;
    }
}

// -------------------------------------------------------------
// 6. Interactive Ingredient Modal
// -------------------------------------------------------------
async function showIngredientConflictDetails(ingredient) {
    const modal = document.getElementById("conflict-detail-modal");
    const modalTitle = document.getElementById("modal-title");
    const ingredientsContainer = document.getElementById("modal-conflict-ingredients");
    const productsContainer = document.getElementById("modal-conflict-products");

    modalTitle.textContent = `Incompatibility Report: ${ingredient}`;
    ingredientsContainer.innerHTML = '<div class="spinner-container"><div class="spinner"></div></div>';
    productsContainer.innerHTML = '<div class="spinner-container"><div class="spinner"></div></div>';

    modal.classList.remove("hidden");

    try {
        const response = await fetch(`${API_BASE}/api/conflicts/details?ingredient=${encodeURIComponent(ingredient)}`);
        if (!response.ok) throw new Error("Failed to load details");
        
        const details = await response.json();
        
        ingredientsContainer.innerHTML = "";
        productsContainer.innerHTML = "";

        if (details.length === 0) {
            ingredientsContainer.innerHTML = '<p class="no-items-placeholder">No conflicts found for this ingredient.</p>';
            productsContainer.innerHTML = '<p class="no-items-placeholder">No conflicting products in catalog.</p>';
            return;
        }

        const uniqueProducts = new Set();
        
        details.forEach(item => {
            // Render conflicting ingredient card
            const conflictCard = document.createElement("div");
            const severityClass = item.severity.toLowerCase(); // 'high' or 'moderate'
            conflictCard.className = `modal-conflict-item ${severityClass}`;
            conflictCard.innerHTML = `
                <div class="modal-conflict-item-title">
                    <span>${item.conflictingIngredient}</span>
                    <span class="badge ${severityClass === 'high' ? 'badge-high' : 'badge-warn'}">${item.severity.toUpperCase()}</span>
                </div>
                <div class="modal-conflict-item-reason">${item.reason}</div>
            `;
            ingredientsContainer.appendChild(conflictCard);

            // Collect products
            if (Array.isArray(item.products)) {
                item.products.forEach(pName => {
                    if (pName) uniqueProducts.add(pName);
                });
            }
        });

        // Render products
        if (uniqueProducts.size === 0) {
            productsContainer.innerHTML = '<p class="no-items-placeholder">No products in current catalog contain these conflicting ingredients.</p>';
        } else {
            uniqueProducts.forEach(prodName => {
                const prodItem = document.createElement("div");
                prodItem.className = "modal-product-item";
                prodItem.textContent = prodName;
                productsContainer.appendChild(prodItem);
            });
        }

    } catch (err) {
        ingredientsContainer.innerHTML = `<p class="error-text">Error loading details: ${err.message}</p>`;
        productsContainer.innerHTML = "";
    }
}

function closeModal(event) {
    const modal = document.getElementById("conflict-detail-modal");
    if (!event || event.target === modal || event.target.classList.contains("close-modal-btn")) {
        modal.classList.add("hidden");
    }
}

// -------------------------------------------------------------
// App Initialization
// -------------------------------------------------------------
document.addEventListener("DOMContentLoaded", () => {
    // Start on recommendations tab
    switchTab("recommendations");
});