describe('BolClick public web smoke tests', () => {
  it('loads the landing page and navigates to login', () => {
    cy.visit('/');
    cy.contains('h1', 'Punto de venta gratuito y Gestión de Inventario').should('be.visible');
    cy.get('a[href="/login"]').first().click();
    cy.location('pathname').should('eq', '/login');
    cy.contains('h2', 'Bienvenido').should('be.visible');
    cy.get('input[type="email"]').should('be.visible').and('have.attr', 'required');
    cy.get('input[type="password"]').should('be.visible').and('have.attr', 'required');
    cy.get('form').should(($form) => expect($form[0].checkValidity()).to.equal(false));
  });

  it('renders registration and enforces required fields without submitting', () => {
    cy.visit('/register');
    cy.contains('h2', 'Crea tu Espacio').should('be.visible');
    cy.get('form input[required]').should('have.length.at.least', 8);
    cy.get('form').should(($form) => expect($form[0].checkValidity()).to.equal(false));
    cy.contains('button[type="submit"]', 'Solicitar Registro de Tienda').should('be.visible');
  });
});
