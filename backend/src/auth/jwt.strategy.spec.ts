import { Repository } from 'typeorm';
import { User } from '../users/user.entity';
import { JwtStrategy } from './jwt.strategy';

describe('JwtStrategy: acceso actual a sucursales', () => {
  it('expone la sucursal y el rol actuales del usuario, no el rol antiguo del token', async () => {
    const strategy = new JwtStrategy({
      findOne: jest.fn().mockResolvedValue({
        id: 'usuario-1', isActive: true, role: 'VENDEDOR', sucursal_id: 'sucursal-a',
      }),
    } as unknown as Repository<User>, null as never);
    await expect(strategy.validate({
      sub: 'usuario-1', role: 'OWNER', tenantId: 'tienda-a',
    })).resolves.toEqual(expect.objectContaining({
      role: 'VENDEDOR', sucursalId: 'sucursal-a', tenantId: 'tienda-a',
    }));
  });
});
