import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import InfoBasica from './InfoBasica';
import MetricaCoherencia from './MetricaCoherencia';
import HistorialCoherencia from './HistorialCoherencia';
import ParticipacionCiudadana from './ParticipacionCiudadana';
import { PerfilPolitico } from './type_perfil_politico';

const PerfilPoliticoPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [perfil, setPerfil] = useState<PerfilPolitico | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const fetchPerfil = async () => {
    setIsLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/politicos/${id}`);
      if (!response.ok) throw new Error("Perfil no encontrado");
      const data = await response.json();
      setPerfil(data);
    } catch (error) {
      console.error("Error al cargar el perfil:", error);
      navigate('/'); // Volver al directorio si hay error
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchPerfil();
  }, [id]);

  const handleAddComentario = async (texto: string, puntaje: number) => {
    try {
      // POST Comentario
      await fetch(`http://localhost:8080/api/politicos/${id}/comentarios`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ texto, usuarioId: 1 }) // Usuario hardcodeado temporalmente hasta tener auth
      });

      // POST Calificación
      await fetch(`http://localhost:8080/api/politicos/${id}/calificaciones`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ puntaje, usuarioId: 1 })
      });

      fetchPerfil(); // Recargar datos
    } catch (error) {
      console.error("Error al publicar comentario:", error);
    }
  };

  if (isLoading) {
    return (
      <div className="max-w-5xl mx-auto animate-pulse">
        <div className="bg-white h-64 rounded-xl mb-8 border border-slate-200"></div>
        <div className="bg-white h-48 rounded-xl mb-8 border border-slate-200"></div>
        <div className="bg-white h-96 rounded-xl border border-slate-200"></div>
      </div>
    );
  }

  if (!perfil) return null;

  return (
    <div className="max-w-5xl mx-auto">
      <button 
        onClick={() => navigate('/')}
        className="flex items-center gap-2 text-slate-500 hover:text-primary-navy font-bold text-sm mb-6 transition-colors group"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="group-hover:-translate-x-1 transition-transform"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
        Volver al Directorio
      </button>

      <InfoBasica 
        nombre={perfil.nombre}
        organizacion={perfil.organizacion}
        cargo={perfil.cargo}
        patrimonio={perfil.patrimonio}
        fotoUrl={perfil.fotoUrl}
        estaActivo={perfil.estaActivo}
        antecedentes={perfil.antecedentes}
      />

      <MetricaCoherencia 
        porcentaje={perfil.porcentajeCoherencia}
        estadoEtiqueta={perfil.estadoEtiqueta}
      />

      <HistorialCoherencia 
        historial={perfil.historial}
      />

      <ParticipacionCiudadana 
        comentarios={perfil.comentarios}
        onAddComentario={handleAddComentario}
      />
    </div>
  );
};

export default PerfilPoliticoPage;
