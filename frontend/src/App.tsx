import { Header } from './components/Header';
import { LoginForm } from './components/LoginForm';
import { StocksTable } from './components/StocksTable';
import { useAuth } from './context/AuthContext';

function App() {
  const { token } = useAuth();

  return (
    <div className="app-shell">
      <Header />
      {!token && <LoginForm />}
      <StocksTable />
    </div>
  );
}

export default App;

