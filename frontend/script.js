// API Base URL
const API_BASE_URL = "http://localhost:8080/api";

// DOM Elements
document.addEventListener("DOMContentLoaded", function () {
  initializeEventListeners();
});

// Initialize all event listeners
function initializeEventListeners() {
  // Form submissions
  document
    .getElementById("create-account-form")
    .addEventListener("submit", handleCreateAccount);
  document
    .getElementById("view-account-form")
    .addEventListener("submit", handleViewAccount);
  document
    .getElementById("deposit-transaction-form")
    .addEventListener("submit", handleDeposit);
  document
    .getElementById("withdraw-transaction-form")
    .addEventListener("submit", handleWithdraw);
  document
    .getElementById("history-form")
    .addEventListener("submit", handleViewHistory);
  document
    .getElementById("delete-account-form")
    .addEventListener("submit", handleDeleteAccount);
}

// Tab Management
function showTab(tabName) {
  // Hide all tab contents
  const tabContents = document.querySelectorAll(".tab-content");
  tabContents.forEach((tab) => tab.classList.remove("active"));

  // Remove active class from all tab buttons
  const tabButtons = document.querySelectorAll(".tab-button");
  tabButtons.forEach((button) => button.classList.remove("active"));

  // Show selected tab content
  document.getElementById(tabName).classList.add("active");

  // Add active class to clicked button
  event.target.classList.add("active");
}

// Transaction Tab Management
function showTransactionTab(tabName) {
  // Hide all transaction forms
  const transactionForms = document.querySelectorAll(".transaction-form");
  transactionForms.forEach((form) => form.classList.remove("active"));

  // Remove active class from all transaction tabs
  const transactionTabs = document.querySelectorAll(".transaction-tab");
  transactionTabs.forEach((tab) => tab.classList.remove("active"));

  // Show selected transaction form
  document.getElementById(tabName + "-form").classList.add("active");

  // Add active class to clicked tab
  event.target.classList.add("active");
}

// Message Display Functions
function showMessage(message, type = "info") {
  const messageContainer = document.getElementById("message-container");

  const messageDiv = document.createElement("div");
  messageDiv.className = `message ${type}`;

  let icon = "fas fa-info-circle";
  if (type === "success") icon = "fas fa-check-circle";
  if (type === "error") icon = "fas fa-exclamation-circle";

  messageDiv.innerHTML = `
        <i class="${icon}"></i>
        <span>${message}</span>
    `;

  messageContainer.appendChild(messageDiv);

  // Auto remove message after 5 seconds
  setTimeout(() => {
    messageDiv.remove();
  }, 5000);
}

// API Helper Functions
async function makeAPIRequest(url, method = "GET", data = null) {
  try {
    const options = {
      method: method,
      headers: {
        "Content-Type": "application/json",
      },
    };

    if (data) {
      options.body = JSON.stringify(data);
    }

    const response = await fetch(url, options);
    const result = await response.json();

    return result;
  } catch (error) {
    console.error("API Request Error:", error);
    throw new Error(
      "Network error occurred. Please check if the server is running."
    );
  }
}

// Account Management Functions
async function handleCreateAccount(event) {
  event.preventDefault();

  const name = document.getElementById("account-name").value.trim();
  const balance = parseFloat(document.getElementById("initial-balance").value);

  if (!name) {
    showMessage("Please enter a valid name", "error");
    return;
  }

  if (isNaN(balance) || balance < 0) {
    showMessage("Please enter a valid initial balance", "error");
    return;
  }

  try {
    const result = await makeAPIRequest(`${API_BASE_URL}/accounts`, "POST", {
      name: name,
      balance: balance,
    });

    if (result.success) {
      showMessage(
        `Account created successfully! Account ID: ${result.accountId}`,
        "success"
      );
      document.getElementById("create-account-form").reset();
    } else {
      showMessage(result.error || "Failed to create account", "error");
    }
  } catch (error) {
    showMessage(error.message, "error");
  }
}

async function handleViewAccount(event) {
  event.preventDefault();

  const accountId = parseInt(document.getElementById("view-account-id").value);

  if (isNaN(accountId) || accountId <= 0) {
    showMessage("Please enter a valid account ID", "error");
    return;
  }

  try {
    const result = await makeAPIRequest(
      `${API_BASE_URL}/accounts/${accountId}`
    );

    if (result.success) {
      // Display account information
      document.getElementById("display-account-id").textContent = result.id;
      document.getElementById("display-account-name").textContent = result.name;
      document.getElementById(
        "display-account-balance"
      ).textContent = `$${result.balance.toFixed(2)}`;

      document.getElementById("account-details").style.display = "block";
      showMessage("Account details loaded successfully", "success");
    } else {
      document.getElementById("account-details").style.display = "none";
      showMessage(result.error || "Account not found", "error");
    }
  } catch (error) {
    showMessage(error.message, "error");
  }
}

// Transaction Functions
async function handleDeposit(event) {
  event.preventDefault();

  const accountId = parseInt(
    document.getElementById("deposit-account-id").value
  );
  const amount = parseFloat(document.getElementById("deposit-amount").value);

  if (isNaN(accountId) || accountId <= 0) {
    showMessage("Please enter a valid account ID", "error");
    return;
  }

  if (isNaN(amount) || amount <= 0) {
    showMessage("Please enter a valid deposit amount", "error");
    return;
  }

  try {
    const result = await makeAPIRequest(
      `${API_BASE_URL}/accounts/${accountId}/deposit`,
      "POST",
      {
        amount: amount,
      }
    );

    if (result.success) {
      showMessage(
        `Deposit successful! $${amount.toFixed(
          2
        )} deposited to account ${accountId}`,
        "success"
      );
      document.getElementById("deposit-transaction-form").reset();
    } else {
      showMessage(result.error || "Deposit failed", "error");
    }
  } catch (error) {
    showMessage(error.message, "error");
  }
}

async function handleWithdraw(event) {
  event.preventDefault();

  const accountId = parseInt(
    document.getElementById("withdraw-account-id").value
  );
  const amount = parseFloat(document.getElementById("withdraw-amount").value);

  if (isNaN(accountId) || accountId <= 0) {
    showMessage("Please enter a valid account ID", "error");
    return;
  }

  if (isNaN(amount) || amount <= 0) {
    showMessage("Please enter a valid withdrawal amount", "error");
    return;
  }

  try {
    const result = await makeAPIRequest(
      `${API_BASE_URL}/accounts/${accountId}/withdraw`,
      "POST",
      {
        amount: amount,
      }
    );

    if (result.success) {
      showMessage(
        `Withdrawal successful! $${amount.toFixed(
          2
        )} withdrawn from account ${accountId}`,
        "success"
      );
      document.getElementById("withdraw-transaction-form").reset();
    } else {
      showMessage(result.error || "Withdrawal failed", "error");
    }
  } catch (error) {
    showMessage(error.message, "error");
  }
}

// Transaction History
async function handleViewHistory(event) {
  event.preventDefault();

  const accountId = parseInt(
    document.getElementById("history-account-id").value
  );

  if (isNaN(accountId) || accountId <= 0) {
    showMessage("Please enter a valid account ID", "error");
    return;
  }

  try {
    const result = await makeAPIRequest(
      `${API_BASE_URL}/accounts/${accountId}/transactions`
    );

    if (result.success) {
      displayTransactionHistory(result.transactions, accountId);
      showMessage("Transaction history loaded successfully", "success");
    } else {
      document.getElementById("transaction-history").style.display = "none";
      showMessage(
        result.error || "Failed to load transaction history",
        "error"
      );
    }
  } catch (error) {
    showMessage(error.message, "error");
  }
}

// Display transaction history in table
function displayTransactionHistory(transactions, accountId) {
  document.getElementById("transaction-history").style.display = "block";
  const tbody = document.getElementById("history-tbody");

  if (transactions.length === 0) {
    tbody.innerHTML = `
            <tr>
                <td colspan="2" style="text-align: center; padding: 20px; color: #6c757d;">
                    <i class="fas fa-info-circle"></i> 
                    No transactions found for account ${accountId}
                </td>
            </tr>
        `;
    return;
  }

  tbody.innerHTML = "";
  transactions.forEach((transaction) => {
    const row = document.createElement("tr");
    
    // Handle timestamp - show "N/A" since your database doesn't have timestamp column
    const formattedDate = "N/A";

    const typeClass = transaction.type === "DEPOSIT" ? "success" : "warning";
    const typeIcon =
      transaction.type === "DEPOSIT" ? "fas fa-arrow-down" : "fas fa-arrow-up";

    row.innerHTML = `
            <td>
                <span class="transaction-type ${typeClass}">
                    <i class="${typeIcon}"></i> ${transaction.type}
                </span>
            </td>
            <td class="amount">$${transaction.amount.toFixed(2)}</td>
        `;
    tbody.appendChild(row);
  });
}

// Account Deletion
async function handleDeleteAccount(event) {
  event.preventDefault();

  const accountId = parseInt(
    document.getElementById("delete-account-id").value
  );

  if (isNaN(accountId) || accountId <= 0) {
    showMessage("Please enter a valid account ID", "error");
    return;
  }

  // Confirmation dialog
  const confirmed = confirm(
    `Are you sure you want to delete account ${accountId}? This action cannot be undone.`
  );

  if (!confirmed) {
    return;
  }

  try {
    const result = await makeAPIRequest(
      `${API_BASE_URL}/accounts/${accountId}`,
      "DELETE"
    );

    if (result.success) {
      showMessage(`Account ${accountId} deleted successfully`, "success");
      document.getElementById("delete-account-form").reset();

      // Clear account details if it was the same account
      const displayedAccountId =
        document.getElementById("display-account-id").textContent;
      if (displayedAccountId == accountId) {
        document.getElementById("account-details").style.display = "none";
      }
    } else {
      showMessage(result.error || "Failed to delete account", "error");
    }
  } catch (error) {
    showMessage(error.message, "error");
  }
}

// Utility Functions
function formatCurrency(amount) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(amount);
}

function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

// Auto-refresh account balance (optional feature)
function startAutoRefresh(accountId) {
  setInterval(async () => {
    try {
      const result = await makeAPIRequest(
        `${API_BASE_URL}/accounts/${accountId}`
      );
      if (result.success) {
        document.getElementById("display-account-balance").textContent =
          formatCurrency(result.balance);
      }
    } catch (error) {
      // Silently fail for auto-refresh
      console.log("Auto-refresh failed:", error);
    }
  }, 30000); // Refresh every 30 seconds
}
