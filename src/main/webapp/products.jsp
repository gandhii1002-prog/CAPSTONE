<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GandhiMart - Products</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background: #f5f5f5;
        }

        header {
            background: #222;
            color: white;
            padding: 20px;
            text-align: center;
        }

        .container {
            max-width: 1100px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .filters {
            background: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 25px;
        }

        .filters input,
        .filters select {
            padding: 10px;
            margin: 5px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        .stock-filter {
            margin: 5px;
        }

        .filters button {
            padding: 10px 18px;
            margin: 5px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        .products {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
            gap: 20px;
        }

        .product-card {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
        }

        .product-card h3 {
            margin-top: 0;
        }

        .price {
            font-weight: bold;
            margin: 10px 0;
        }

        .stock {
            color: #555;
        }

        .rating {
            margin: 12px 0;
            font-weight: bold;
        }

        .review-section {
            margin-top: 15px;
            border-top: 1px solid #ddd;
            padding-top: 12px;
        }

        .review {
            margin-top: 10px;
            padding: 10px;
            background: #f8f8f8;
            border-radius: 5px;
        }

        .review-author {
            font-weight: bold;
        }

        .review-comment {
            margin-top: 5px;
            white-space: pre-wrap;
        }

        .no-reviews {
            color: #666;
            font-size: 14px;
        }

        .error {
            color: #b00020;
            margin-bottom: 15px;
        }

        .empty {
            text-align: center;
            color: #666;
            grid-column: 1 / -1;
        }
    </style>
</head>

<body>

<header>
    <h1>GandhiMart</h1>
    <p>
        <a href="login.jsp">
            Login
        </a>
        |
        <a href="cart.jsp">
            My Cart
        </a>
    </p>
</header>

<div class="container">

    <div class="filters">
        <input
                type="text"
                id="keyword"
                placeholder="Search products">

        <input
            type="text"
            id="category"
            placeholder="Category">

        <input
                type="number"
                id="minPrice"
                placeholder="Min price"
                min="0"
                step="0.01">

        <input
                type="number"
                id="maxPrice"
                placeholder="Max price"
                min="0"
                step="0.01">

        <label class="stock-filter">
            <input
                    type="checkbox"
                    id="inStock">
            In-stock only
        </label>

        <select id="sort">
            <option value="NEWEST">Newest</option>
            <option value="PRICE_ASC">Price: Low to High</option>
            <option value="PRICE_DESC">Price: High to Low</option>
        </select>

        <button onclick="loadProducts()">
            Search
        </button>

        <button onclick="clearFilters()">
            Clear
        </button>
    </div>

    <div id="error" class="error"></div>

    <div id="products" class="products">
        <div class="empty">Loading products...</div>
    </div>

</div>

<script>
    async function loadProducts() {

        const keyword =
            document.getElementById("keyword").value.trim();

        const minPrice =
            document.getElementById("minPrice").value.trim();

        const maxPrice =
            document.getElementById("maxPrice").value.trim();

        const category =
            document.getElementById("category").value.trim();

        const inStock =
            document.getElementById("inStock").checked;

        const sort =
            document.getElementById("sort").value;

        const params = new URLSearchParams();

        if (keyword) {
            params.append("keyword", keyword);
        }

        if (category) {
            params.append("category", category);
        }

        if (minPrice) {
            params.append("minPrice", minPrice);
        }

        if (maxPrice) {
            params.append("maxPrice", maxPrice);
        }

        if (inStock) {
            params.append("inStock", "true");
        }

        if (sort !== "NEWEST") {
            params.append("sort", sort);
        }

        const url =
            "<%= request.getContextPath() %>/api/v1/products"
            + (params.toString()
                ? "?" + params.toString()
                : "");

        const productsContainer =
            document.getElementById("products");

        const errorContainer =
            document.getElementById("error");

        errorContainer.textContent = "";
        productsContainer.innerHTML =
            '<div class="empty">Loading products...</div>';

        try {

            const response = await fetch(url);

            const data = await response.json();

            if (!response.ok) {
                throw new Error(
                    data.error || "Unable to retrieve products"
                );
            }

            renderProducts(data.products);

        } catch (error) {

            productsContainer.innerHTML = "";

            errorContainer.textContent =
                error.message;
        }
    }

    function renderProducts(products) {

        const container =
            document.getElementById("products");

        if (!products || products.length === 0) {

            container.innerHTML =
                '<div class="empty">No products found.</div>';

            return;
        }

        container.innerHTML = "";

        products.forEach(product => {

            const card =
                document.createElement("div");

            card.className = "product-card";

            const name =
                document.createElement("h3");

            name.textContent =
                product.name;

            const description =
                document.createElement("p");

            description.textContent =
                product.description || "";

            const price =
                document.createElement("div");

            price.className = "price";

            price.textContent =
                "₹" + Number(product.price).toFixed(2);

            const stock =
                document.createElement("div");

            stock.className = "stock";

            stock.textContent =
                "Stock: " + product.stock;

            const addButton =
                document.createElement("button");

            addButton.textContent =
                "Add to Cart";

            addButton.onclick =
                () => addToCart(product.id);

                const rating = document.createElement("div");
                rating.className = "rating";
                rating.textContent = "Loading rating...";

                const reviewSection = document.createElement("div");
                reviewSection.className = "review-section";

            card.appendChild(name);
            card.appendChild(description);
            card.appendChild(price);
            card.appendChild(stock);
            card.appendChild(addButton);
            card.appendChild(rating);
            card.appendChild(reviewSection);

            container.appendChild(card);

            loadProductReviews(product.id, rating, reviewSection);
        });
    }

    async function loadProductReviews(
            productId,
            ratingElement,
            reviewContainer) {

        try {
            const response = await fetch(
                    "api/v1/reviews?productId=" +
                    encodeURIComponent(productId));
            const data = await response.json();

            if (!response.ok) {
                ratingElement.textContent = "Rating unavailable";
                return;
            }

            const average = Number(data.averageRating || 0);
            const count = Number(data.count || 0);

            ratingElement.textContent = count === 0
                    ? "No reviews yet"
                    : "★ " + average.toFixed(1) + " / 5 (" + count +
                      " review" + (count === 1 ? "" : "s") + ")";

            renderReviews(data.reviews || [], reviewContainer);
        } catch (error) {
            ratingElement.textContent = "Rating unavailable";
        }
    }

    function renderReviews(reviews, container) {

        container.innerHTML = "";

        if (!reviews || reviews.length === 0) {
            const empty = document.createElement("div");
            empty.className = "no-reviews";
            empty.textContent = "No reviews yet.";
            container.appendChild(empty);
            return;
        }

        reviews.slice(0, 3).forEach(review => {
            const item = document.createElement("div");
            item.className = "review";

            const author = document.createElement("div");
            author.className = "review-author";
            author.textContent = review.reviewerName || "Buyer";

            const stars = document.createElement("div");
            const rating = Number(review.rating);
            stars.textContent = "★".repeat(rating) + "☆".repeat(5 - rating);

            const comment = document.createElement("div");
            comment.className = "review-comment";
            comment.textContent = review.comment || "";

            item.appendChild(author);
            item.appendChild(stars);
            item.appendChild(comment);
            container.appendChild(item);
        });

        if (reviews.length > 3) {
            const more = document.createElement("div");
            more.className = "no-reviews";
            more.textContent = "Showing the latest 3 reviews.";
            container.appendChild(more);
        }
    }

    function clearFilters() {

        document.getElementById("keyword").value = "";
        document.getElementById("category").value = "";
        document.getElementById("minPrice").value = "";
        document.getElementById("maxPrice").value = "";
        document.getElementById("inStock").checked = false;
        document.getElementById("sort").value = "NEWEST";

        loadProducts();
    }

    async function addToCart(productId) {

        const quantity =
            prompt(
                "Enter quantity:",
                "1"
            );

        if (quantity === null) {
            return;
        }

        const formData =
            new URLSearchParams();

        formData.append(
            "productId",
            productId
        );

        formData.append(
            "quantity",
            quantity
        );

        try {

            const response =
                await fetch(
                    "api/v1/cart/add",
                    {
                        method: "POST",
                        headers: {
                            "Content-Type":
                                "application/x-www-form-urlencoded"
                        },
                        body: formData
                    }
                );

            const data =
                await response.json();

            alert(
                data.message ||
                data.error ||
                "Cart operation completed"
            );

        } catch (error) {

            alert(
                "Unable to add product to cart"
            );
        }
    }

    loadProducts();
</script>

</body>
</html>