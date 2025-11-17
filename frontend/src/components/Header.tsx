import { useAuth } from '../context/AuthContext';

export const Header = () => {
  const { username, logout, token, expiresAt } = useAuth();

  return (
    <header className="card hero">
      <div>
        <p className="badge">ENSET / ADIA Demo</p>
        <h1>Tableau de bord boursier</h1>
        <p>
          Authentification JWT (port 8090) + API Gateway (port 8888) + Stock Service (port 8081).
        </p>
      </div>
      {token && (
        <div className="session">
          <p>
            Connecté en tant que <strong>{username}</strong>
          </p>
          {expiresAt && (
            <span>Expire le {new Date(expiresAt).toLocaleString('fr-MA')}</span>
          )}
          <button onClick={logout}>Se déconnecter</button>
        </div>
      )}
    </header>
  );
};

