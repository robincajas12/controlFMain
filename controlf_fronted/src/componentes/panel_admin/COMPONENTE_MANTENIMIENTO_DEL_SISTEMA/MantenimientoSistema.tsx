import React from 'react';

/** Snapshot del estado operativo del sistema mostrado en el panel de administración. */
interface MantenimientoSistemaProps {
  info: {
    id: string;
    titulo: string;
    codigoReferencia: string;
    estadoBaseDeDatos: boolean;
    estadoEtiqueta: string;
    fechaUltimoRespaldo: string;
    cargaServidorPorcentaje: number;
    accionesDisponibles: string[];
  };
  onAccion: (accion: string) => void;
}

/**
 * Panel de solo lectura con el estado de la base de datos, el último
 * respaldo y la carga del servidor, junto a una explicación en lenguaje
 * simple para usuarios no técnicos.
 */
const MantenimientoSistema: React.FC<MantenimientoSistemaProps> = ({ info }) => {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden mb-8">
      <div className="px-8 py-6 border-b border-slate-100 bg-slate-50/50">
        <h4 className="text-sm font-bold text-primary-navy uppercase tracking-wide flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 20V10"/><path d="M18 20V4"/><path d="M6 20v-4"/></svg>
          Mantenimiento del Sistema ({info.codigoReferencia})
        </h4>
      </div>

      <div className="p-8 grid grid-cols-1 md:grid-cols-2 gap-12">
        <div className="space-y-6">
          <div className="flex items-center justify-between p-4 bg-slate-50 rounded-xl border border-slate-100">
            <span className="text-xs font-bold text-slate-500 uppercase">Estado del sistema</span>
            <span className={`px-3 py-1 rounded-full text-[10px] font-black border ${info.estadoBaseDeDatos ? 'bg-success-green/10 text-success-green border-success-green/20' : 'bg-danger-red/10 text-danger-red border-danger-red/20'}`}>
              {info.estadoBaseDeDatos ? 'EN LÍNEA' : 'SIN CONEXIÓN'}
            </span>
          </div>

          <div className="flex items-center justify-between p-4 bg-slate-50 rounded-xl border border-slate-100">
            <span className="text-xs font-bold text-slate-500 uppercase">Último Respaldo</span>
            <span className="text-sm font-bold text-slate-700">{info.fechaUltimoRespaldo}</span>
          </div>

          <div className="space-y-2 p-4 bg-slate-50 rounded-xl border border-slate-100">
            <div className="flex justify-between items-end mb-2">
              <span className="text-xs font-bold text-slate-500 uppercase">Carga del Servidor</span>
              <span className="text-sm font-black text-primary-navy">{info.cargaServidorPorcentaje}%</span>
            </div>
            <div className="w-full h-2 bg-slate-200 rounded-full overflow-hidden">
              <div 
                className={`h-full transition-all duration-1000 ${info.cargaServidorPorcentaje > 80 ? 'bg-danger-red' : info.cargaServidorPorcentaje > 50 ? 'bg-warning-amber' : 'bg-accent-blue'}`}
                style={{ width: `${info.cargaServidorPorcentaje}%` }}
              ></div>
            </div>
          </div>
        </div>

        <div className="rounded-xl border border-slate-200 bg-slate-50 p-5 text-sm text-slate-600">
          <h5 className="text-xs font-black uppercase tracking-wide text-primary-navy mb-3">¿Qué significa esta sección?</h5>
          <p className="leading-relaxed">
            Aquí puedes revisar de un vistazo si el sistema está funcionando con normalidad,
            cuándo se guardó la última copia de la información y qué tan cargado está el servicio.
          </p>
          <p className="mt-3 leading-relaxed">
            Si el estado aparece <span className="font-bold text-danger-red">Sin conexión</span> o la carga se mantiene muy alta
            durante mucho tiempo, comunícate con el equipo de soporte técnico.
          </p>
        </div>
      </div>
    </div>
  );
};

export default MantenimientoSistema;
