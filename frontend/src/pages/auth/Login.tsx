import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Zap, Lock, Mail, ArrowRight, ShieldCheck, TrendingUp } from 'lucide-react';
import { useAuthStore } from '../../stores/authStore';
import { authApi } from '../../api/authApi';
import { Input } from '../../components/common/Input';
import { Button } from '../../components/common/Button';

const loginSchema = z.object({
  email: z.string().email('Please enter a valid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export const Login: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuthStore();
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: 'demo@portfolio.com',
      password: 'password123',
    },
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      setIsLoading(true);
      setError(null);
      const res = await authApi.login({
        email: data.email,
        password: data.password,
      });

      // Update state store
      login(res);
      navigate('/dashboard');
    } catch (err: any) {
      console.error('Login error:', err);
      setError(
        err.response?.data?.error?.message ||
          err.response?.data?.message ||
          'Failed to sign in. Please verify your credentials or server connectivity.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  const handleQuickDemoFill = () => {
    setValue('email', 'demo@portfolio.com');
    setValue('password', 'password123');
  };

  return (
    <div className="flex min-h-screen bg-fin-bg text-fin-text">
      {/* Left panel: Financial branding */}
      <div className="hidden lg:flex lg:w-1/2 flex-col justify-between p-12 bg-fin-panel border-r border-fin-border relative overflow-hidden">
        <div className="absolute -top-32 -left-32 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute -bottom-32 -right-32 w-96 h-96 bg-emerald-600/10 rounded-full blur-3xl pointer-events-none" />

        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-fin-blue text-white shadow-lg shadow-blue-500/25">
            <Zap className="w-5 h-5" />
          </div>
          <div>
            <h1 className="text-lg font-bold tracking-tight text-white leading-tight">FINEDGE</h1>
            <p className="text-xs font-mono text-fin-blue font-semibold uppercase">
              Indian Portfolio Risk &amp; Performance Terminal
            </p>
          </div>
        </div>

        <div className="my-auto space-y-6 max-w-md">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-fin-card border border-fin-border text-xs text-fin-emerald font-mono">
            <ShieldCheck className="w-3.5 h-3.5" />
            <span>Institutional-Grade Analytics</span>
          </div>

          <h2 className="text-3xl font-bold tracking-tight text-white leading-snug">
            Advanced Multi-Asset Risk Analytics & Quantitative Optimization
          </h2>

          <p className="text-sm text-fin-muted leading-relaxed">
            Mark-to-market live valuations, Markowitz Efficient Frontier optimization, Monte Carlo
            probabilistic fan simulations, and stress testing across historical crises.
          </p>

          <div className="grid grid-cols-2 gap-4 pt-4 border-t border-fin-border">
            <div>
              <div className="text-xl font-bold font-mono text-white">99.5%</div>
              <div className="text-xs text-fin-muted">Parametric & Historical VaR</div>
            </div>
            <div>
              <div className="text-xl font-bold font-mono text-emerald-400">10,000+</div>
              <div className="text-xs text-fin-muted">Monte Carlo Iterations</div>
            </div>
          </div>
        </div>

        <div className="text-xs text-fin-muted flex items-center justify-between font-mono">
          <span>SPRING BOOT 3 &bull; FASTAPI &bull; REACT 19</span>
          <span>v1.0.0-PROD</span>
        </div>
      </div>

      {/* Right panel: Login form */}
      <div className="flex-1 flex flex-col justify-center px-6 py-12 lg:px-16">
        <div className="mx-auto w-full max-w-sm">
          <div className="mb-8">
            <h2 className="text-2xl font-bold text-white tracking-tight">Sign in to Terminal</h2>
            <p className="text-xs text-fin-muted mt-1.5">
              Enter your credentials to access your multi-asset investment portfolios
            </p>
          </div>

          <div className="mb-6 p-4 rounded-xl bg-fin-card border border-fin-blue/40 shadow-lg">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-bold text-white flex items-center gap-1.5">
                <span className="inline-block w-2 h-2 rounded-full bg-fin-emerald animate-pulse" />
                Live Demo Portfolio Ready
              </span>
              <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-fin-blue/15 text-fin-blue font-semibold">
                INR (₹) &bull; NSE
              </span>
            </div>
            <p className="text-[11px] text-fin-muted mb-3 leading-relaxed">
              Experience the full Indian investment terminal with 11 pre-loaded bluechips and Nifty 50 benchmarks.
            </p>
            <Button
              type="button"
              variant="primary"
              className="w-full text-xs font-bold shadow-md shadow-blue-500/20"
              onClick={() => {
                setValue('email', 'demo@portfolio.com');
                setValue('password', 'password123');
                onSubmit({ email: 'demo@portfolio.com', password: 'password123' });
              }}
              isLoading={isLoading}
              rightIcon={<ArrowRight className="w-4 h-4" />}
            >
              🚀 Instant Demo Login (1-Click)
            </Button>
          </div>

          <div className="relative flex py-2 items-center">
            <div className="flex-grow border-t border-fin-border"></div>
            <span className="flex-shrink mx-3 text-[11px] text-fin-muted font-mono uppercase">Or Sign In with Credentials</span>
            <div className="flex-grow border-t border-fin-border"></div>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 mt-3">
            <Input
              label="Email Address"
              type="email"
              placeholder="name@fund.com"
              leftIcon={<Mail className="w-4 h-4" />}
              error={errors.email?.message}
              {...register('email')}
            />

            <Input
              label="Password"
              type="password"
              placeholder="••••••••"
              leftIcon={<Lock className="w-4 h-4" />}
              error={errors.password?.message}
              {...register('password')}
            />

            <div className="flex items-center justify-between text-xs pt-1">
              <button
                type="button"
                onClick={handleQuickDemoFill}
                className="text-fin-blue hover:underline font-mono text-[11px]"
              >
                Auto-fill Demo Credentials
              </button>
              <span className="text-fin-muted text-[11px]">Forgot password?</span>
            </div>

            <Button
              type="submit"
              variant="outline"
              className="w-full mt-2"
              isLoading={isLoading}
              rightIcon={<ArrowRight className="w-4 h-4" />}
            >
              Sign In to Terminal
            </Button>
          </form>

          <div className="mt-6 text-center text-xs text-fin-muted">
            Don't have an analyst account?{' '}
            <Link to="/register" className="text-fin-blue hover:underline font-medium">
              Create an account
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
