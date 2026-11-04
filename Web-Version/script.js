function updateLength() {
  document.getElementById("lengthValue").innerText = document.getElementById("length").value;
}

function generatePassword() {
  const length = document.getElementById("length").value;
  const useUpper = document.getElementById("uppercase").checked;
  const useNumbers = document.getElementById("numbers").checked;
  const useSymbols = document.getElementById("symbols").checked;

  let lower = "abcdefghijklmnopqrstuvwxyz";
  let upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  let numbers = "0123456789";
  let symbols = "!@#$%^&*()_+[]{}|;:,.<>?";

  let allChars = lower;
  if (useUpper) allChars += upper;
  if (useNumbers) allChars += numbers;
  if (useSymbols) allChars += symbols;

  let password = "";
  for (let i = 0; i < length; i++) {
    password += allChars.charAt(Math.floor(Math.random() * allChars.length));
  }

  document.getElementById("password").value = password;
  checkStrength(password);
}

function checkStrength(password) {
  let strength = document.getElementById("strength");
  let score = 0;

  if (password.length >= 8) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/[0-9]/.test(password)) score++;
  if (/[^A-Za-z0-9]/.test(password)) score++;

  if (score <= 1) {
    strength.innerText = "Strength: Very Weak";
    strength.className = "strength weak";
  } else if (score === 2) {
    strength.innerText = "Strength: Medium";
    strength.className = "strength medium";
  } else {
    strength.innerText = "Strength: Strong";
    strength.className = "strength strong";
  }
}

function copyPassword() {
  const passwordField = document.getElementById("password");
  passwordField.select();
  document.execCommand("copy");
  alert("Password copied to clipboard!");
}
