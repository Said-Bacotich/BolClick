# Evidencias SQA de BolClick

Corte de revisión: 28 de octubre de 2026.

## Archivos

- `Plan_SQA_Agil_BolClick.docx`: plan ágil reconstruido, auditoría interna, resultados, métricas, riesgos, acciones y matriz para Teams Planner.
- `Registro_Casos_Prueba_BolClick.csv`: ocho casos trazables, con prioridad, pasos y resultado esperado.

## Pruebas que ya están en el código

- Backend Jest: `backend/src/app.controller.spec.ts`, `backend/src/stock/stock.service.spec.ts`, `backend/src/ventas/business-rules.spec.ts` y `backend/src/ventas/ventas.service.spec.ts`.
- Pruebas de navegador: `frontend/cypress/e2e/public-entry.cy.js`.
- CI: `.github/workflows/quality.yml` ejecuta pruebas backend, compilación, integración contra PostgreSQL desechable y pruebas Cypress.
- La prueba de integración solo acepta `DB_DATABASE=bolclick_test`; rechaza `mall_db`.

## Resultados verificados localmente

- Backend Jest: 4 suites, 30 pruebas aprobadas.
- Cobertura backend: 22,73 % líneas; 21,29 % ramas; 10,09 % funciones.
- Build backend y frontend: aprobados.
- Navegador: aprobados manualmente la página pública, la navegación al login y la presencia de ocho campos obligatorios en el registro; no se enviaron formularios.
- Cypress local: el ejecutable descargado no pudo iniciar en este Windows (código 2147483651). El workflow de GitHub Actions está configurado para ejecutar los casos en un runner compatible; no se debe registrar como aprobado hasta observar el resultado del workflow.
- Integración PostgreSQL: no se ejecutó contra la base local. CI usa un servicio PostgreSQL efímero para que las pruebas no puedan afectar `mall_db`.
- Teams Planner y TestLink: la integración se documenta y el registro CSV está disponible, pero no se crearon tarjetas ni se conectó una instancia del curso durante esta revisión.
