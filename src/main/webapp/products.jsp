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

        .filters input {
            padding: 10px;
            margin: 5px;
            border: 1px solid #ccc;
            border-radius: 4px;
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

        const params = new URLSearchParams();

        if (keyword) {
            params.append("keyword", keyword);
        }

        if (minPrice) {
            params.append("minPrice", minPrice);
        }

        if (maxPrice) {
            params.append("maxPrice", maxPrice);
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

            card.appendChild(name);
            card.appendChild(description);
            card.appendChild(price);
            card.appendChild(stock);
            card.appendChild(addButton);

            container.appendChild(card);
        });
    }

    function clearFilters() {

        document.getElementById("keyword").value = "";
        document.getElementById("minPrice").value = "";
        document.getElementById("maxPrice").value = "";

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