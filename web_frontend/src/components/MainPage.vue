<script setup>
import {ref, onMounted} from 'vue';
import axios from 'axios';
import Swal from 'sweetalert2';
import { watch } from 'vue';

const xCoordinate = ref('0');
const yCoordinate = ref('0');
const radius = ref('1');
const results = ref([]);
const canvas = ref(null);

const validateInput = () => {
  if (
      isNaN(parseFloat(xCoordinate.value)) ||
      isNaN(parseFloat(yCoordinate.value)) ||
      isNaN(parseFloat(radius.value)) ||
      parseFloat(radius.value) < 1 ||
      parseFloat(radius.value) > 3 ||
      (yCoordinate.value < -3) ||
      (yCoordinate.value > 3) ||
      (xCoordinate.value < -3) ||
      (xCoordinate.value > 3)
  ) {
    Swal.fire({
      title: 'Некорректные данные!',
      text: 'Пожалуйста, введите корректные данные',
      icon: 'error',
      confirmButtonText: 'OK',
      customClass: {
        popup: 'custom-swal-popup',
        title: 'custom-swal-title',
        confirmButton: 'custom-swal-button'
      }
    });
    return false;
  }
  return true;
};


const sendCoordinates = async (x, y, r) => {
  if (!validateInput()) return;

  try {
    const username = localStorage.getItem('username');
    const password = localStorage.getItem('password');
    if (!username) {
      Swal.fire({
        title: 'Ошибка!',
        text: 'Имя пользователя не найдено. Пожалуйста, войдите снова.',
        icon: 'error',
        confirmButtonText: 'OK',
        customClass: {
        popup: 'custom-swal-popup',
        title: 'custom-swal-title',
        confirmButton: 'custom-swal-button'
      }
      });
      return;
    }

    const response = await axios.get('/api/check', {
      params: { x, y, r, username, password }
    });

    const success = response.data.hit;
    results.value.push({ x, y, radius: r, result: success ? 'Попадание' : 'Промах' });

    Swal.fire({
      title: success ? 'Попадание' : 'Промах',
      icon: success ? 'success' : 'error',
      confirmButtonText: 'OK',
      customClass: {
        popup: 'custom-swal-popup',
        title: 'custom-swal-title',
        confirmButton: 'custom-swal-button'
      }
    });


    drawGraph();
  } catch (error) {
    Swal.fire({
      title: 'Ошибка!',
      text: 'Не удалось отправить данные на сервер:\n' + error,
      icon: 'error',
      confirmButtonText: 'OK',
      customClass: {
        popup: 'custom-swal-popup',
        title: 'custom-swal-title',
        confirmButton: 'custom-swal-button'
      }
    });
  }
};

const handleFormSubmit = () => {
  sendCoordinates(parseFloat(xCoordinate.value), parseFloat(yCoordinate.value), parseFloat(radius.value));
};

const handleCanvasClick = (event) => {
  const rect = canvas.value.getBoundingClientRect();
  const x = ((event.clientX - rect.left) / rect.width - 0.5) * 8;
  const y = -((event.clientY - rect.top) / rect.height - 0.5) * 8;
  const r = parseFloat(radius.value);

  sendCoordinates(x.toFixed(2), y.toFixed(2), r);
};

const loadPoints = async () => {
  try {
    const username = localStorage.getItem('username');
    const password = localStorage.getItem('password');
    if (!username) {
      Swal.fire({
        title: 'Ошибка!',
        text: 'Имя пользователя не найдено. Пожалуйста, войдите снова.',
        icon: 'error',
        confirmButtonText: 'OK',
        customClass: {
        popup: 'custom-swal-popup',
        title: 'custom-swal-title',
        confirmButton: 'custom-swal-button'
      }
      });
      return;
    }

    const response = await axios.get('/api/getPoints', {
      params: { username, password }
    });

    response.data.forEach(point => {
      results.value.push({
        x: point.x,
        y: point.y,
        radius: point.r,
        result: point.hit ? 'Попадание' : 'Промах',
      });
      drawPoint(point.x, point.y, point.hit ? 'green' : 'red');
    });
  } catch (error) {
    Swal.fire({
      title: 'Ошибка!',
      text: 'Не удалось получить данные с сервера:\n' + error,
      icon: 'error',
      confirmButtonText: 'OK',
      customClass: {
        popup: 'custom-swal-popup',
        title: 'custom-swal-title',
        confirmButton: 'custom-swal-button'
      }
    });
  }
}



const logout = () => {
  Swal.fire({
    title: 'Вы действительно хотите выйти?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Да, выйти',
    cancelButtonText: 'Отмена',
    reverseButtons: true,
    customClass: {
        popup: 'custom-swal-popup',
        title: 'custom-swal-title',
        confirmButton: 'custom-swal-button'
      }
  }).then((result) => {
    if (result.isConfirmed) {
      localStorage.removeItem('username');
      localStorage.removeItem('password');
      window.location.href = '/';
    }
  });
};

const drawGraph = () => {
  let radiusValue = parseFloat(radius.value);

  if (radiusValue < 1 || radiusValue > 3) {
    Swal.fire({
      title: 'Некорректный радиус!',
      text: 'Радиус должен быть от 1 до 3',
      icon: 'error',
      confirmButtonText: 'OK',
      customClass: {
        popup: 'custom-swal-popup',
        title: 'custom-swal-title',
        confirmButton: 'custom-swal-button'
      }
    });
    return;
  }
  const ctx = canvas.value.getContext('2d');
  const width = canvas.value.width;
  const height = canvas.value.height;
  const r = parseFloat(radius.value);

  ctx.clearRect(0, 0, width, height);

  const centerX = width / 2;
  const centerY = height / 2;

  const scale = width / 8;

  ctx.fillStyle = 'rgba(0, 0, 255, 0.5)';

  // Прямоугольник в правом верхнем углу
  ctx.fillRect(centerX - r * scale, centerY, (r) * scale, (r/2) * scale);

  // Четверть круга в нижнем правом углу
  ctx.beginPath();
  ctx.moveTo(centerX, centerY);
  ctx.arc(centerX, centerY, scale * r, 1.5 * Math.PI, 2 * Math.PI);
  ctx.closePath();
  ctx.fill();

// Зеркально противоположный треугольник в нижнем правом углу
  ctx.beginPath();
  ctx.moveTo(centerX, centerY);
  ctx.lineTo(centerX + r * scale, centerY);          // Смещение вправо
  ctx.lineTo(centerX, centerY + r * scale / 2);      // Смещение вниз
  ctx.closePath();
  ctx.fill();


  // Отрисовка координатных осей
  ctx.strokeStyle = 'black';
  ctx.beginPath();
  ctx.moveTo(centerX, 0);
  ctx.lineTo(centerX, height);
  ctx.moveTo(0, centerY);
  ctx.lineTo(width, centerY);
  ctx.stroke();

  // Отметки на осях
  ctx.fillStyle = 'black';
  ctx.font = '12px Arial';
  for (let i = -4; i <= 4; i++) {
    const offsetX = centerX + i * scale;
    const offsetY = centerY - i * scale;
    if (i !== 0) {
      ctx.fillText(i, offsetX - 5, centerY + 15); // Ось X
      ctx.fillText(i, centerX + 5, offsetY + 5); // Ось Y
    }
  }

  results.value.forEach(result => {
    if (result.radius == radius.value) {
      drawPoint(result.x, result.y, result.result === 'Попадание' ? 'green' : 'red');
    }
  });
};

const drawPoint = (x, y, color) => {
  const ctx = canvas.value.getContext('2d');
  const width = canvas.value.width;
  const height = canvas.value.height;
  const r = parseFloat(radius.value);
  const scale = width / 8;

  const centerX = width / 2;
  const centerY = height / 2;

  ctx.fillStyle = color;
  ctx.beginPath();
  ctx.arc(centerX + x * scale, centerY - y * scale, 3, 0, 2 * Math.PI);
  ctx.fill();
};

watch(radius, drawGraph);
onMounted(() => {
  drawGraph();
});

onMounted(() => {
  const username = localStorage.getItem('username');
  const password = localStorage.getItem('password');

  if (!username || !password) {
    Swal.fire({
      title: 'Вы не авторизованы!((',
      text: 'Пожалуйста, войдите в систему.',
      icon: 'warning',
      confirmButtonText: 'OK',
    }).then(() => {
      window.location.href = '/';
    });
  } else {
    try {
      const response =  axios.post('/api/login', {
        username: username,
        password: password,
      });
      loadPoints();
      drawGraph();
    } catch (error) {
      Swal.fire({
        title: 'Ошибка входа!',
        text: 'Используемый пароль неправильный. Выполните вход заново',
        icon: 'error',
        confirmButtonText: 'OK',
      }).then(() => {
        localStorage.removeItem('username');
        localStorage.removeItem('password');
        window.location.href = '/';
      });
    }
  }
});
</script>

<script>
export default {
  data() {
    return {
      username: localStorage.getItem('username') || 'Гость',
    };
  },
};
</script>


<template>
  <div>
    <h1>Главная страница</h1>

    <p>Вы вошли как: {{ username }}</p>

    <div>
      <label for="x-coordinate">Координата X:</label>
      <input
        id="x-coordinate"
        v-model="xCoordinate"
        type="number"
        placeholder="Введите X (-3 ... 3)" />
    </div>

    <div>
      <label for="y-coordinate">Координата Y:</label>
      <input
        id="y-coordinate"
        v-model="yCoordinate"
        type="number"
        placeholder="Введите Y (-3 ... 3)" />
    </div>

    <div>
      <label for="radius">Радиус:</label>
      <input
        id="radius"
        v-model="radius"
        type="number"
        placeholder="Введите R (1 ... 3)" />
    </div>


    <button @click="handleFormSubmit">Отправить данные</button>

    <canvas ref="canvas" id="plane" width="500" height="500" @click="handleCanvasClick"></canvas>
    <div class="result-container">
      <table border="1">
        <thead>
        <tr>
          <th>X</th>
          <th>Y</th>
          <th>R</th>
          <th>Результат</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(result, index) in results" :key="index">
          <td>{{ result.x }}</td>
          <td>{{ result.y }}</td>
          <td>{{ result.radius }}</td>
          <td>{{ result.result }}</td>
        </tr>
        </tbody>
      </table>
      <button @click="logout">Выйти</button>
    </div>
  </div>
</template>

<style scoped>

.result-container {
  width: 100%;
  max-width: 1000px;
  margin: 50px auto;
  padding: 30px 40px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9));
  border-radius: 15px;
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
}

.result-container:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 30px rgba(232, 29, 530, 0.3);
}
.container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  border-radius: 20px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease-in-out;
}

.container:hover {
  transform: scale(1.02);
}


h1 {
  font-size: 2.5rem;
  color: #333;
  text-align: center;
}

p {
  text-align: center;
  font-size: 1.1rem;
  color: #555;
}

label {
  display: block;
  font-size: 1rem;
  color: #555;
  margin-bottom: 8px;
}

input {
  width: 100%;
  padding: 12px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 10px;
  box-sizing: border-box;
  margin-bottom: 20px;
}

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

canvas {
  width: 100%;
  max-width: 500px;
  height: auto;
  border: 2px solid #333;
  border-radius: 15px;
  margin-top: 20px;
  display: block;
  margin-left: auto;
  margin-right: auto;
}



table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  margin-bottom: 10px; /* Добавляет небольшой отступ между таблицей и кнопкой */
}

table, th, td {
  border: 1px solid #ccc;
}

th, td {
  padding: 12px;
  text-align: center;
}

th {
  background-color: #f4f7fa;
}

button.logout {
  background-color: #dc3545;
}

button.logout:hover {
  background-color: #c82333;
}

@media (max-width: 1109px) and (min-width: 765px) {
  .container {
    max-width: 90%;
  }
}

@media (max-width: 764px) {
  .container {
    max-width: 100%;
    padding: 10px;
  }
  h1 {
    font-size: 2rem;
  }
  button {
    padding: 12px;
  }
  canvas {
    height: 350px;
  }
  table {
    font-size: 0.9rem;
  }
}


</style>

