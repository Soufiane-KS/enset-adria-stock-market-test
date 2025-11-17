const AUTH_API = import.meta.env.VITE_AUTH_API ?? 'http://localhost:8090';
const GATEWAY_API = import.meta.env.VITE_GATEWAY_API ?? 'http://localhost:8888';

export type LoginRequest = {
  username: string;
  password: string;
};

export type LoginResponse = {
  token: string;
  expiresAt: number;
};

export type Stock = {
  id: string;
  date: string;
  openValue: number;
  closeValue: number;
  volume: number;
  companyId: string;
  currentPrice?: number;
};

export async function login(request: LoginRequest): Promise<LoginResponse> {
  const response = await fetch(`${AUTH_API}/api/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    throw new Error('Identifiants invalides');
  }

  return response.json();
}

export async function fetchStocks(token: string): Promise<Stock[]> {
  const response = await fetch(`${GATEWAY_API}/stock-service/api/stocks`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    throw new Error('Impossible de charger les cotations');
  }

  return response.json();
}

