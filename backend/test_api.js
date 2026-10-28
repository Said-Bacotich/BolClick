const baseURL = process.env.API_BASE_URL || 'http://localhost:3000/api';
const email = process.env.TEST_EMAIL;
const password = process.env.TEST_PASSWORD;

async function test() {
  if (!email || !password) {
    console.error('Set TEST_EMAIL and TEST_PASSWORD before running this script.');
    process.exitCode = 1;
    return;
  }

  try {
    const res = await fetch(`${baseURL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    const authData = await res.json();
    const token = authData.access_token;

    if (!token) {
      console.log('No token:', authData);
      return;
    }

    const salesRes = await fetch(`${baseURL}/ventas`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const data = await salesRes.json();
    console.log(JSON.stringify(data[0], null, 2));
  } catch (error) {
    console.error(error.message);
  }
}

test();