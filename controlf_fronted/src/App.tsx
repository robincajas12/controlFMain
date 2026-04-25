import { Routes, Route, Link, useNavigate, useParams } from 'react-router-dom'

function Home() {
  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '20px', fontFamily: 'sans-serif', lineHeight: '1.6' }}>
      <h1>React Router Tutorial</h1>
      <p>Welcome! This is a minimal setup to help you understand how routing works in React.</p>

      <section>
        <h2>1. Basic Setup</h2>
        <p>In <code>src/main.tsx</code>, we've wrapped the <code>&lt;App /&gt;</code> with <code>&lt;BrowserRouter&gt;</code>. This enables routing across your entire application.</p>
      </section>

      <section>
        <h2>2. Defining Routes</h2>
        <p>Inside <code>App.tsx</code>, we use <code>&lt;Routes&gt;</code> and <code>&lt;Route&gt;</code> to map paths to components:</p>
        <pre style={{ background: '#f4f4f4', padding: '10px', borderRadius: '5px', color: '#333' }}>
{`<Routes>
  <Route path="/" element={<Home />} />
  <Route path="/about" element={<About />} />
  <Route path="/user/:id" element={<UserProfile />} />
</Routes>`}
        </pre>
      </section>

      <section>
        <h2>3. Navigation</h2>
        <p>Instead of <code>&lt;a&gt;</code> tags, use the <code>&lt;Link&gt;</code> component to navigate without reloading the page:</p>
        <div style={{ display: 'flex', gap: '10px' }}>
          <Link to="/about" style={{ color: '#007bff' }}>Go to About Page</Link>
          <Link to="/user/123" style={{ color: '#007bff' }}>Go to User 123</Link>
        </div>
      </section>

      <section>
        <h2>4. Hooks</h2>
        <ul>
          <li><strong>useNavigate:</strong> For programmatic navigation (e.g., after submitting a form).</li>
          <li><strong>useParams:</strong> To access dynamic parameters in the URL.</li>
        </ul>
      </section>
    </div>
  )
}

function About() {
  return (
    <div style={{ padding: '20px' }}>
      <h1>About Page</h1>
      <p>This is a secondary route.</p>
      <Link to="/" style={{ color: '#007bff' }}>Back to Home</Link>
    </div>
  )
}

function UserProfile() {
  const { id } = useParams()
  return (
    <div style={{ padding: '20px' }}>
      <h1>User Profile</h1>
      <p>Viewing profile for User ID: <strong>{id}</strong></p>
      <Link to="/" style={{ color: '#007bff' }}>Back to Home</Link>
    </div>
  )
}

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/about" element={<About />} />
      <Route path="/user/:id" element={<UserProfile />} />
    </Routes>
  )
}

export default App
