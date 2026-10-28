const { Client } = require('pg');

async function getTenants() {
  const client = new Client({
    user: process.env.DB_USERNAME || 'postgres',
    host: process.env.DB_HOST || 'localhost',
    database: process.env.DB_DATABASE || 'mall_db',
    password: process.env.DB_PASSWORD || '',
    port: Number(process.env.DB_PORT) || 5432,
  });

  try {
    await client.connect();
    const res = await client.query('SELECT name, domain FROM tenants');
    console.log('--- LISTA DE TIENDAS ---');
    if (res.rows.length === 0) {
      console.log('No hay tiendas registradas aún.');
    } else {
      res.rows.forEach(row => {
        console.log(`Nombre: ${row.name} | Dominio: ${row.domain}`);
      });
    }
  } catch (err) {
    console.error('Error al conectar con la base de datos:', err.message);
  } finally {
    await client.end();
  }
}

getTenants();
