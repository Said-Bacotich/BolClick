const { Client } = require('pg');
const client = new Client({
  user: process.env.DB_USERNAME || 'postgres',
  password: process.env.DB_PASSWORD || '',
  host: process.env.DB_HOST || 'localhost',
  port: Number(process.env.DB_PORT) || 5432,
  database: process.env.DB_DATABASE || 'mall_db'
});

client.connect()
  .then(() => client.query('SELECT id, name, "imagen_url" FROM productos LIMIT 20'))
  .then(res => {
    console.table(res.rows);
    return client.end();
  })
  .catch(err => {
    console.error(err);
    process.exit(1);
  });
