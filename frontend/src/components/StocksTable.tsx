import { useEffect, useMemo, useState } from 'react';
import type { Stock } from '../api';
import { fetchStocks } from '../api';
import { useAuth } from '../context/AuthContext';

export const StocksTable = () => {
  const { token } = useAuth();
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadStocks = async () => {
    if (!token) return;
    setLoading(true);
    setError(null);
    try {
      const data = await fetchStocks(token);
      setStocks(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur inconnue');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStocks();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  const groupedByCompany = useMemo(() => {
    return stocks.reduce<Record<string, Stock[]>>((acc, stock) => {
      acc[stock.companyId] = acc[stock.companyId] ?? [];
      acc[stock.companyId].push(stock);
      return acc;
    }, {});
  }, [stocks]);

  if (!token) {
    return null;
  }

  return (
    <div className="card" style={{ marginTop: '2rem' }}>
      <div className="table-header">
        <div>
          <h2>Cotations récentes</h2>
          <p>Via Gateway `http://localhost:8888/stock-service/api/stocks`</p>
        </div>
        <button onClick={loadStocks} disabled={loading}>
          Rafraîchir
        </button>
      </div>

      {loading && <p>Chargement des données...</p>}
      {error && <p className="error">{error}</p>}

      {!loading && !error && (
        <div className="companies">
          {Object.entries(groupedByCompany).map(([companyId, entries]) => (
            <section key={companyId} className="company-section">
              <header>
                <strong>{companyId}</strong>
                <span>
                  Dernière mise à jour:{' '}
                  {new Date(entries[0]?.date ?? Date.now()).toLocaleDateString('fr-MA')}
                </span>
              </header>

              <div className="table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>Date</th>
                      <th>Ouverture</th>
                      <th>Clôture</th>
                      <th>Volume</th>
                      <th>Prix courant</th>
                    </tr>
                  </thead>
                  <tbody>
                    {entries.map((stock) => (
                      <tr key={stock.id}>
                        <td>{new Date(stock.date).toLocaleDateString('fr-MA')}</td>
                        <td>{stock.openValue.toFixed(2)} MAD</td>
                        <td>{stock.closeValue.toFixed(2)} MAD</td>
                        <td>{stock.volume.toLocaleString('fr-MA')}</td>
                        <td>{(stock.currentPrice ?? stock.closeValue).toFixed(2)} MAD</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>
          ))}
        </div>
      )}
    </div>
  );
};

