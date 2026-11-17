import { Repository } from 'typeorm';
import { LoteIngreso } from './lote-ingreso.entity';
import { SourcingService } from './sourcing.service';

describe('Recordatorios de vencimiento: acceso por sucursal', () => {
  const find = jest.fn();
  const service = new SourcingService(
    { find } as unknown as Repository<LoteIngreso>,
    null as never,
    null as never,
  );

  beforeEach(() => {
    find.mockReset();
    find.mockResolvedValue([]);
  });

  it.each(['VENDEDOR', 'SUPERVISOR'])(
    'limita %s al tenant y a su sucursal autenticada', async role => {
      await service.getVencimientos('tienda-a', { role, sucursalId: 'sucursal-a' });
      expect(find).toHaveBeenCalledWith(expect.objectContaining({
        where: expect.objectContaining({
          tenant_id: 'tienda-a',
          stock: { tenant_id: 'tienda-a', sucursal_id: 'sucursal-a' },
        }),
      }));
    },
  );

  it.each(['OWNER', 'SUPER_ADMIN'])(
    'permite todas las sucursales de su tienda a %s', async role => {
      await service.getVencimientos('tienda-a', { role });
      expect(find).toHaveBeenCalledWith(expect.objectContaining({
        where: expect.objectContaining({
          tenant_id: 'tienda-a', stock: { tenant_id: 'tienda-a' },
        }),
      }));
    },
  );

  it.each([null, undefined, ''])(
    'no consulta lotes para un empleado sin sucursal (%s)', async sucursalId => {
      await expect(service.getVencimientos('tienda-a', {
        role: 'VENDEDOR', sucursalId,
      })).resolves.toEqual([]);
      expect(find).not.toHaveBeenCalled();
    },
  );

  it('solo devuelve los datos de presentacion, sin cantidades ni stock', async () => {
    find.mockResolvedValue([{
      id: 'lote-1', fechaVencimiento: '2026-11-17', cantidad: 100,
      stock: {
        sucursal_id: 'sucursal-a', cantidadActual: 80,
        producto: { name: 'Leche', precioCosto: 12 },
        sucursal: { name: 'Centro' },
      },
    }]);
    await expect(service.getVencimientos('tienda-a', {
      role: 'VENDEDOR', sucursalId: 'sucursal-a',
    })).resolves.toEqual([{
      id: 'lote-1', fechaVencimiento: '2026-11-17', sucursal_id: 'sucursal-a',
      producto: { name: 'Leche' }, sucursal: { name: 'Centro' },
    }]);
  });

  it('propaga los errores de consulta para que la app pueda mostrar el error', async () => {
    find.mockRejectedValue(new Error('sin conexion'));
    await expect(service.getVencimientos('tienda-a', { role: 'OWNER' }))
      .rejects.toThrow('sin conexion');
  });
});
