import React, { useState, useEffect } from 'react';
import PanelControlSeguridad from './COMPONENTE_PANEL_DE_CONTROL/PanelControlSeguridad';
import MotorCoherencia from './COMPONENTE_MOTOR_COHERENCIA/MotorCoherencia';
import MantenimientoSistema from './COMPONENTE_MANTENIMIENTO_DEL_SISTEMA/MantenimientoSistema';

const AdminPage: React.FC = () => {
  const [seguridad, setSeguridad] = useState<any>(null);
  const [mantenimiento, setMantenimiento] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(true);

  const fetchData = async () => {
    setIsLoading(true);
    try {
      const [segRes, mantRes] = await Promise.all([
        fetch('http://localhost:8080/api/admin/panel'),
        fetch('http://localhost:8080/api/admin/mantenimiento')
      ]);
      
      setSeguridad(await segRes.json());
      setMantenimiento(await mantRes.json());
    } catch (error) {
      console.error("Error al cargar datos administrativos:", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleAccionMantenimiento = async (accion: string) => {
    let endpoint = '';
    switch (accion) {
      case 'BACKUP': endpoint = '/mantenimiento/respaldo'; break;
      case 'CACHE_CLEAR': endpoint = '/mantenimiento/limpiar-cache'; break;
      case 'IMPORT_LEYES': endpoint = '/importar-leyes'; break;
    }

    try {
      await fetch(`http://localhost:8080/api/admin${endpoint}`, { method: 'POST' });
      alert(`Acción ${accion} ejecutada correctamente`);
      fetchData(); // Refrescar info
    } catch (error) {
      console.error(`Error al ejecutar ${accion}:`, error);
    }
  };

  if (isLoading) {
    return (
      <div className="max-w-6xl mx-auto animate-pulse space-y-8">
        <div className="bg-white h-48 rounded-xl border border-slate-200"></div>
        <div className="bg-white h-96 rounded-xl border border-slate-200"></div>
        <div className="bg-white h-64 rounded-xl border border-slate-200"></div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto pb-12">
      <div className="mb-8">
        <h2 className="text-2xl font-black text-primary-navy uppercase tracking-tighter">Panel de Control y Administración</h2>
        <p className="text-slate-500">Gestión de auditoría ciudadana, seguridad y mantenimiento de infraestructura</p>
      </div>

      <PanelControlSeguridad 
        titulo={seguridad.tituloSeccion}
        opciones={seguridad.opciones}
      />

      <MotorCoherencia />

      <MantenimientoSistema 
        info={mantenimiento}
        onAccion={handleAccionMantenimiento}
      />
    </div>
  );
};

export default AdminPage;
