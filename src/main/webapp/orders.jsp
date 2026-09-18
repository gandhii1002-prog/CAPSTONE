<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <title>GandhiMart - My Orders</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 30px;
            background: #f5f5f5;
        }

        .container {
            max-width: 900px;
            margin: auto;
        }

        .order {
            background: white;
            padding: 20px;
            margin-bottom: 15px;
            border-radius: 8px;
        }

        .review-card {
            background: white;
            padding: 20px;
            margin-bottom: 15px;
            border-left: 4px solid #222;
            border-radius: 8px;
        }

        .stars {
            display: flex;
            gap: 5px;
            margin: 10px 0;
        }

        .star-button {
            border: 1px solid #ccc;
            background: white;
            font-size: 22px;
            cursor: pointer;
            border-radius: 4px;
            width: 42px;
            height: 42px;
        }

        .star-button.selected {
            background: #ffd54f;
        }

        textarea {
            width: 100%;
            box-sizing: border-box;
            min-height: 100px;
            resize: vertical;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        .submit-review {
            margin-top: 10px;
            padding: 10px 18px;
            border: none;
            border-radius: 5px;
            background: #222;
            color: white;
            cursor: pointer;
        }

        .status {
            font-weight: bold;
        }

        .total {
            font-size: 20px;
            font-weight: bold;
        }

        .error {
            color: red;
        }

        .success {
            color: green;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>My Orders</h1>

    <p>
        <a href="products.jsp">
            Continue Shopping
        </a>
        |
        <a href="cart.jsp">
            My Cart
        </a>
    </p>

    <div id="message"></div>

    <div id="orders"></div>

    <section>
        <h2>Products You Can Review</h2>
        <div id="reviewable">Loading reviewable products...</div>
    </section>

</div>

<script>

    async function loadOrders() {

        const message =
                document.getElementById("message");

        const container =
                document.getElementById("orders");

        message.textContent = "";
        container.innerHTML = "";

        try {

            const response =
                    await fetch(
                            "api/v1/orders/my"
                    );

            const data =
                    await response.json();

            if (!response.ok) {

                message.className = "error";

                message.textContent =
                        data.error ||
                        "Unable to load orders";

                return;
            }

            if (!data.orders ||
                    data.orders.length === 0) {

                container.textContent =
                        "You have no orders yet.";

                return;
            }

            data.orders.forEach(order => {

                const card =
                        document.createElement("div");

                card.className = "order";

                const title =
                        document.createElement("h3");

                title.textContent =
                        "Order #" +
                        order.id;

                const status =
                        document.createElement("p");

                status.className = "status";

                status.textContent =
                        "Status: " +
                        order.status;

                const total =
                        document.createElement("p");

                total.className = "total";

                total.textContent =
                        "Total: ₹" +
                        Number(
                                order.totalAmount
                        ).toFixed(2);

                const date =
                        document.createElement("p");

                date.textContent =
                        "Placed: " +
                        order.createdAt;

                card.appendChild(title);
                card.appendChild(status);
                card.appendChild(total);
                card.appendChild(date);

                container.appendChild(card);
            });

        } catch (error) {

            message.className = "error";

            message.textContent =
                    "Unable to connect to server.";
        }
    }

    async function loadReviewableProducts() {

        const container =
                document.getElementById("reviewable");

        try {
            const response = await fetch("api/v1/reviews/reviewable");
            const data = await response.json();

            if (response.status === 401) {
                container.textContent =
                        "Please log in as a buyer to review purchased products.";
                return;
            }

            if (!response.ok) {
                container.textContent =
                        data.error || "Unable to load reviewable products.";
                return;
            }

            if (!data.products || data.products.length === 0) {
                container.textContent =
                        "You have no products waiting for review.";
                return;
            }

            container.innerHTML = "";
            data.products.forEach(product => createReviewCard(product, container));
        } catch (error) {
            container.textContent = "Unable to load reviewable products.";
        }
    }

    function createReviewCard(product, container) {

        const card = document.createElement("div");
        card.className = "review-card";

        const title = document.createElement("h3");
        title.textContent = product.name;

        const description = document.createElement("p");
        description.textContent = product.description || "";

        const ratingLabel = document.createElement("div");
        ratingLabel.textContent = "Your Rating:";

        const stars = document.createElement("div");
        stars.className = "stars";
        let selectedRating = 0;

        for (let rating = 1; rating <= 5; rating++) {
            const button = document.createElement("button");
            button.type = "button";
            button.className = "star-button";
            button.textContent = "★";
            button.onclick = () => {
                selectedRating = rating;
                stars.querySelectorAll(".star-button").forEach((item, index) => {
                    item.classList.toggle("selected", index < rating);
                });
            };
            stars.appendChild(button);
        }

        const comment = document.createElement("textarea");
        comment.placeholder =
                "Write your review (optional, maximum 1000 characters)...";
        comment.maxLength = 1000;

        const submit = document.createElement("button");
        submit.type = "button";
        submit.className = "submit-review";
        submit.textContent = "Submit Review";

        const result = document.createElement("div");

        submit.onclick = async () => {
            if (selectedRating === 0) {
                result.className = "error";
                result.textContent = "Please select a rating from 1 to 5.";
                return;
            }

            submit.disabled = true;

            try {
                const formData = new URLSearchParams();
                formData.append("productId", product.id);
                formData.append("rating", selectedRating);
                formData.append("comment", comment.value.trim());

                const response = await fetch("api/v1/reviews", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: formData
                });

                const data = await response.json();
                if (!response.ok) {
                    result.className = "error";
                    result.textContent =
                            data.error || "Unable to submit review.";
                    submit.disabled = false;
                    return;
                }

                result.className = "success";
                result.textContent = "Review submitted successfully.";
                card.remove();
                if (!document.querySelector(".review-card")) {
                    container.textContent =
                            "You have no products waiting for review.";
                }
            } catch (error) {
                result.className = "error";
                result.textContent = "Unable to connect to server.";
                submit.disabled = false;
            }
        };

        card.appendChild(title);
        card.appendChild(description);
        card.appendChild(ratingLabel);
        card.appendChild(stars);
        card.appendChild(comment);
        card.appendChild(submit);
        card.appendChild(result);
        container.appendChild(card);
    }

    loadOrders();
    loadReviewableProducts();

</script>

</body>

</html>