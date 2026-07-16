import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

/**
 * Página de resumen general: estadísticas clave y actividad reciente de
 * la plataforma, obtenidas de `/api/dashboard/stats`.
 */
const DashboardPage: React.FC = () => {
  const [stats, setStats] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetch('/api/dashboard/stats')
      .then(res => res.json())
      .then(data => {
        setStats(data);
        setIsLoading(false);
      })
      .catch(err => console.error("Error al cargar stats:", err));
  }, []);

  if (isLoading) {
    return (
      <div className="max-w-6xl mx-auto animate-pulse grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[1, 2, 3, 4].map(i => <div key={i} className="bg-white h-32 rounded-xl border border-slate-200"></div>)}
        <div className="col-span-full bg-white h-96 rounded-xl border border-slate-200"></div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto space-y-8 pb-12">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-black text-primary-navy uppercase tracking-tighter">Resumen General de Auditoría</h2>
          <p className="text-slate-500">Métricas clave y actividad reciente de la plataforma</p>
        </div>
        <div className="text-xs font-bold text-slate-400 uppercase tracking-widest bg-slate-100 px-4 py-2 rounded-full border border-slate-200">
          Última actualización: {new Date().toLocaleDateString()}
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 border-b-4 border-b-accent-blue">
          <div className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Políticos Registrados</div>
          <div className="text-3xl font-black text-primary-navy">{stats.totalPoliticos}</div>
        </div>
        
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 border-b-4 border-b-success-green">
          <div className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Leyes Auditadas</div>
          <div className="text-3xl font-black text-primary-navy">{stats.totalLeyes}</div>
        </div>

        <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 border-b-4 border-b-warning-amber">
          <div className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Coherencia Global</div>
          <div className="text-3xl font-black text-primary-navy">{stats.promedioCoherenciaGlobal}%</div>
        </div>

        <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 border-b-4 border-b-primary-navy">
          <div className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Participación Ciudadana</div>
          <div className="text-3xl font-black text-primary-navy">{stats.totalComentarios}</div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
          <div className="px-8 py-6 border-b border-slate-100 flex items-center justify-between">
            <h3 className="font-bold text-primary-navy uppercase tracking-wide flex items-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
              Actividad Reciente
            </h3>
          </div>
          <div className="p-0">
            {stats.actividadReciente.map((act: any, idx: number) => (
              <div key={idx} className="px-8 py-4 border-b border-slate-50 last:border-0 hover:bg-slate-50 transition-colors flex items-start gap-4">
                <div className={`mt-1 p-2 rounded-lg ${act.tipo === 'COMENTARIO' ? 'bg-blue-50 text-blue-500' : 'bg-green-50 text-success-green'}`}>
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                </div>
                <div className="flex-grow">
                  <div className="flex justify-between items-start mb-1">
                    <span className="text-sm font-bold text-slate-700">{act.usuario}</span>
                    <span className="text-[10px] font-black text-slate-400">{act.fecha}</span>
                  </div>
                  <p className="text-xs text-slate-500 line-clamp-1">{act.detalle}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="space-y-6">
          <div className="bg-primary-navy p-8 rounded-2xl text-white shadow-xl relative overflow-hidden group">
             <div className="relative z-10">
               <h4 className="text-xl font-black mb-2 leading-tight">¿Deseas participar en la auditoría?</h4>
               <p className="text-slate-300 text-sm mb-6">Califica el desempeño de tus representantes y comenta sobre las leyes vigentes.</p>
               <Link to="/" className="inline-block px-6 py-3 bg-accent-blue text-white rounded-xl font-black text-xs hover:bg-blue-600 transition-all shadow-lg">
                 EMPEZAR AHORA
               </Link>
             </div>
             <svg className="absolute -right-8 -bottom-8 text-white/5 w-48 h-48 group-hover:scale-110 transition-transform" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4"/><path d="M12 8h.01"/></svg>
          </div>

          <div className="bg-white p-8 rounded-2xl shadow-sm border border-slate-200">
            <h4 className="font-bold text-primary-navy uppercase tracking-wide mb-4">Metodología</h4>
            <ul className="space-y-4">
              {[
                { t: 'Transparencia', d: 'Datos obtenidos de fuentes oficiales gubernamentales.' },
                { t: 'Cruce Técnico', d: 'Algoritmo de vinculación entre promesas y votos.' },
                { t: 'Voz Ciudadana', d: 'Métricas de opinión basadas en usuarios reales.' }
              ].map((item, i) => (
                <li key={i} className="flex gap-3">
                  <div className="w-1.5 h-1.5 rounded-full bg-accent-blue mt-1.5 shrink-0"></div>
                  <div>
                    <div className="text-xs font-bold text-slate-700">{item.t}</div>
                    <p className="text-[10px] text-slate-500">{item.d}</p>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
