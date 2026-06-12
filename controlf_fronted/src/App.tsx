import { Routes, Route, Navigate } from 'react-router-dom'
import MainLayout from './componentes/layout/MainLayout'
import DirectorioPoliticosPage from './componentes/directorio_politicos/DirectorioPoliticosPage'
import PerfilPoliticoPage from './componentes/perfil_politico_screen/PerfilPoliticoPage'
import DirectorioLeyesPage from './componentes/directorio_leyes/DirectorioLeyesPage'
import PerfilLeyPage from './componentes/perfil_ley/PerfilLeyPage'
import AdminPage from './componentes/panel_admin/AdminPage'
import DashboardPage from './componentes/DashboardPage'

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        {/* Ruta principal: Directorio de Políticos */}
        <Route index element={<DirectorioPoliticosPage />} />
        
        {/* Dashboard */}
        <Route path="dashboard" element={<DashboardPage />} />

        {/* Detalle del Político */}
        <Route path="politico/:id" element={<PerfilPoliticoPage />} />

        {/* Directorio de Leyes */}
        <Route path="leyes" element={<DirectorioLeyesPage />} />

        {/* Detalle de la Ley */}
        <Route path="ley/:id" element={<PerfilLeyPage />} />

        {/* Panel de Administración */}
        <Route path="admin" element={<AdminPage />} />
        
        {/* Redirección por defecto */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  )
}

export default App
