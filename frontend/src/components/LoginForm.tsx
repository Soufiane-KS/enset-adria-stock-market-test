import type { FormEvent } from 'react';
import { useState } from 'react';
import { useAuth } from '../context/AuthContext';

type Props = {
  onSuccess?: () => void;
};

export const LoginForm = ({ onSuccess }: Props) => {
  const { login, loading } = useAuth();
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    try {
      await login(form.username.trim(), form.password);
      onSuccess?.();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur inconnue');
    }
  };

  return (
    <div className="card" style={{ marginTop: '1.5rem' }}>
      <h2>Connexion</h2>
      <p>Utilisez admin/admin123, analyst/stocks2024 ou viewer/password.</p>
      <form onSubmit={handleSubmit} className="form-grid">
        <label>
          Nom d&apos;utilisateur
          <input
            type="text"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            required
            placeholder="admin"
          />
        </label>
        <label>
          Mot de passe
          <input
            type="password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
            placeholder="••••••••"
          />
        </label>
        {error && <p className="error">{error}</p>}
        <button type="submit" disabled={loading}>
          {loading ? 'Connexion...' : 'Se connecter'}
        </button>
      </form>
    </div>
  );
};

