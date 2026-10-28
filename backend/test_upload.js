async function testUpload() {
  const email = process.env.TEST_EMAIL;
  const password = process.env.TEST_PASSWORD;
  if (!email || !password) {
    console.error('Set TEST_EMAIL and TEST_PASSWORD before running this script.');
    process.exitCode = 1;
    return;
  }

  try {
    console.log('Attempting to sign in...');
    const loginRes = await fetch('http://localhost:3000/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    const loginData = await loginRes.json();
    if (!loginRes.ok) {
      console.log('Login error:', loginData);
      return;
    }

    const token = loginData.access_token;
    const blob = new Blob(['fake webp content'], { type: 'image/webp' });
    const formData = new FormData();
    formData.append('file', blob, 'test.webp');
    const uploadRes = await fetch('http://localhost:3000/api/productos/upload', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: formData,
    });
    const uploadData = await uploadRes.json();
    console.log(uploadRes.ok ? 'Upload response:' : 'Upload error:', uploadData);
  } catch (error) {
    console.error('Exception:', error.message);
  }
}

testUpload();