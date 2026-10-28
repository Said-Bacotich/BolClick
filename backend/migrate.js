const { Client } = require('pg');

const client = new Client({
  host: process.env.DB_HOST || 'localhost',
  port: Number(process.env.DB_PORT) || 5432,
  user: process.env.DB_USERNAME || 'postgres',
  password: process.env.DB_PASSWORD || '',
  database: process.env.DB_DATABASE || 'mall_db',
});

client.connect()
  .then(() => client.query('UPDATE ventas SET "utilidadTotal" = total * 0.4, "costoTotal" = total * 0.6 WHERE "utilidadTotal" = 0 AND total > 0;'))
  .then(res => {
    console.log(`Updated ${res.rowCount} rows`);
    return client.end();
  })
  .catch(err => {
    console.error(err);
    return client.end();
  });
