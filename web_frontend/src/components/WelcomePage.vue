<script setup>
import { ref } from 'vue';
import axios from 'axios';
import Swal from 'sweetalert2';
import CryptoJS from "crypto-js";

const username = ref('');
const password = ref('');

const handleLogin = async () => {
  try {
    const hashedPassword = CryptoJS.SHA256(password.value).toString(CryptoJS.enc.Base64);

    const response = await axios.post('/api/login', {
      username: username.value,
      password: hashedPassword,
    });

    if (response.status === 200) {
      Swal.fire({
        title: 'Вход успешен!',
        text: 'Добро пожаловать:)',
        icon: 'success',
        confirmButtonText: 'Перейти к главной странице',
      }).then(() => {
        localStorage.setItem('username', username.value);
        localStorage.setItem('password', hashedPassword);
        window.location.href = '/main';
      });
    } else {
      Swal.fire({
        title: 'Ошибка входа!:(',
        text: 'Неправильный пароль',
        icon: 'error',
        confirmButtonText: 'Попробовать снова',
      });
    }
  } catch (error) {
    if (error.response?.status === 401) {
      Swal.fire({
        title: 'Ошибка входа!:(',
        text: 'Неправильное имя пользователя или пароль',
        icon: 'error',
        confirmButtonText: 'Попробовать зарегистрироваться',
      }).then(() => {
        window.location.href = '/register';
      });
    } else {
      console.error('Login error:', error);
      Swal.fire({
        title: 'Ошибка!:(',
        text: 'Во время выполнения возникла ошибка на сервере. Попробуйте позже',
        icon: 'error',
        confirmButtonText: 'OK',
      });
    }
  }
};
</script>

<template>
  <div class="login-container">
    <h3>Коноплянко Сергей Иванович</h3>
    <h3>Группа: P3209</h3>
    <h3>Вариант: 948742</h3>

    <h3>Ввойдите в свой аккаунт</h3>

    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="username">Имя пользователя:</label>
        <input id="username" v-model="username" type="text" placeholder="Введите логин" required/>
      </div>
      <div class="form-group">
        <label for="password">Пароль:</label>
        <input id="password" v-model="password" type="password" placeholder="Введите пароль" required/>
      </div>
      <button type="submit">Войти</button>
    </form>

    <p>Нет аккаунта?
      <router-link to="/register">Зарегистрироваться</router-link>
    </p>
  </div>
</template>

<style scoped>
/* Общие настройки */
body {
  background: #f0f0f0;
  font-family: 'Poppins', sans-serif;
}

/* Контейнер формы входа */
.login-container {
  width: 100%;
  max-width: 500px;
  margin: 50px auto;
  padding: 30px 40px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9));
  border-radius: 15px;
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
}

.login-container:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 30px rgba(232, 29, 530, 0.3);
}

/* Заголовки */
h1, h2, h3, p {
  text-align: center;
  color: #4a148c; /* Темно-фиолетовый */
}

h1, h2 {
  margin-bottom: 10px;
}

h3 {
  margin-top: 20px;
  font-size: 1.4rem;
}

p {
  margin-top: 20px;
  font-size: 1rem;
}

/* Группы формы */
.form-group {
  margin-bottom: 25px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #6a1b9a; /* Средне-фиолетовый */
}

/* Поля ввода */
input {
  width: 90%;
  padding: 14px 20px;
  border: 2px solid #b39ddb; /* Светло-фиолетовый */
  border-radius: 8px;
  background-color: #fafafa;
  font-size: 1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input:focus {
  border-color: #8e24aa; /* Ярко-фиолетовый */
  box-shadow: 0 0 8px rgba(142, 36, 170, 0.5);
  outline: none;
}

/* Кнопка входа */
button {
  width: 100%;
  padding: 15px;
  background: linear-gradient(45deg, #9c27b0, #e040fb);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s ease, transform 0.2s ease, box-shadow 0.3s ease;
}

button:hover {
  background: linear-gradient(45deg, #e040fb, #9c27b0);
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(224, 64, 251, 0.4);
}

/* Ссылка регистрации */
router-link {
  color: #8e24aa; /* Ярко-фиолетовый */
  font-weight: 500;
  transition: color 0.3s ease;
}

router-link:hover {
  color: #d500f9; /* Яркий фиолетовый при наведении */
  text-decoration: underline;
}

/* Медиазапросы для адаптивности */
@media (min-width: 1110px) {
  .login-container {
    max-width: 500px;
  }
}

@media (max-width: 1109px) and (min-width: 765px) {
  .login-container {
    max-width: 90%;
    padding: 25px 30px;
  }
}

@media (max-width: 764px) {
  .login-container {
    max-width: 95%;
    padding: 20px 25px;
  }

  h3 {
    font-size: 1.2rem;
  }

  p {
    font-size: 0.95rem;
  }

  button {
    padding: 12px;
    font-size: 1rem;
  }

  input {
    padding: 12px 18px;
    font-size: 0.95rem;
  }
}
</style>
