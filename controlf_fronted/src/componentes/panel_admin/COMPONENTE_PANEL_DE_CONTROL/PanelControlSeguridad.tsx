import React from 'react';

interface OpcionPanel {
  nombreOpcion: string;
  icono: string;
  notificacionBadge?: number;
}

interface PanelControlSeguridadProps {
  titulo: string;
  opciones: OpcionPanel[];
}

const PanelControlSeguridad: React.FC<PanelControlSeguridadProps> = ({ titulo, opciones }) => {
  const getIcon = (iconName: string) => {
    switch (iconName) {
      case 'user-cog': return <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M10.5 14.5c2.23 0 4.41.4 6.02 1.01"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>;
      case 'flag': return <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"/><line x1="4" y1="22" x2="4" y2="15"/></svg>;
      case 'terminal': return <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="4 17 10 11 4 5"/><line x1="12" y1="19" x2="20" y2="19"/></svg>;
      default: return <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/></svg>;
    }
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden mb-8">
      <div className="px-8 py-4 border-b border-slate-100 bg-slate-50/50">
        <h4 className="text-xs font-black text-slate-400 uppercase tracking-widest">{titulo}</h4>
      </div>
      <div className="p-6 grid grid-cols-1 md:grid-cols-3 gap-4">
        {opciones.map((opcion, index) => (
          <button 
            key={index}
            className="flex items-center justify-between p-4 bg-slate-50 border border-slate-100 rounded-xl hover:border-accent-blue/50 hover:bg-white transition-all group"
          >
            <div className="flex items-center gap-3">
              <div className="p-2 bg-white rounded-lg text-slate-400 group-hover:text-accent-blue shadow-sm border border-slate-100 transition-colors">
                {getIcon(opcion.icono)}
              </div>
              <span className="text-sm font-bold text-slate-600 group-hover:text-primary-navy">{opcion.nombreOpcion}</span>
            </div>
            {opcion.notificacionBadge !== undefined && opcion.notificacionBadge > 0 && (
              <span className="bg-danger-red text-white text-[10px] font-black px-2 py-0.5 rounded-full">
                {opcion.notificacionBadge}
              </span>
            )}
          </button>
        ))}
      </div>
    </div>
  );
};

export default PanelControlSeguridad;
