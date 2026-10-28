const { Client } = require('pg');
const bcrypt = require('bcrypt');
const crypto = require('crypto');

async function seedSuperAdmin() {
  const email = process.env.SUPERADMIN_EMAIL;
  const password = process.env.SUPERADMIN_PASSWORD;
  if (!email || !password) {
    throw new Error('Set SUPERADMIN_EMAIL and SUPERADMIN_PASSWORD before running this script.');
  }

  const client = new Client({
    host: process.env.DB_HOST || 'localhost',
    port: Number(process.env.DB_PORT) || 5432,
    user: process.env.DB_USERNAME || 'postgres',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_DATABASE || 'mall_db',
  });

  try {
    await client.connect();
    const existing = await client.query('SELECT 1 FROM users WHERE email = $1', [email]);
    if (existing.rowCount > 0) {
      console.log(`Super admin already exists: ${email}`);
      return;
    }

    const passwordHash = await bcrypt.hash(password, 10);
    await client.query(
      `INSERT INTO users (id, tenant_id, sucursal_id, name, email, password_hash, role, is_active, created_at, updated_at)
       VALUES ($1, NULL, NULL, $2, $3, $4, 'SUPER_ADMIN', TRUE, NOW(), NOW())`,
      [crypto.randomUUID(), 'Mall Super Admin', email, passwordHash],
    );
    console.log(`Super admin created: ${email}`);
  } finally {
    await client.end();
  }
}

seedSuperAdmin().catch((error) => {
  console.error('Unable to create super admin:', error.message);
  process.exitCode = 1;
});