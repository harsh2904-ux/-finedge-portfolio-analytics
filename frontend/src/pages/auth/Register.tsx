import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Zap, Lock, Mail, User, ArrowRight } from 'lucide-react';
import { useAuthStore } from '../../stores/authStore';
import { authApi } from '../../api/authApi';
import { Input } from '../../components/common/Input';
import { Button } from '../../components/common/Button';

const registerSchema = z.object({
  fullName: z.string().min(2, 'Full name must be at least 2 characters'),
  email: z.string().email('Please enter a valid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  confirmPassword: z.string().min(6, 'Password must be at least 6 characters'),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'Passwords do not match',
  path: ['confirmPassword'],
});

type RegisterFormData = z.infer<typeof registerSchema>;

export const Register: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuthStore();
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormData) => {
    try {
      setIsLoading(true);
      setError(null);
      const res = await authApi.register({
        name: data.fullName,
        fullName: data.fullName,
        email: data.email,
        password: data.password,
      });

      login(res);
      navigate('/dashboard');
    } catch (err: any) {
      console.error('Registration error:', err);
      setError(
        err.response?.data?.message ||
          'Failed to create account. Email may already be in use.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen bg-fin-bg text-fin-text">
      <div className="flex-1 flex flex-col justify-center px-6 py-12 lg:px-16 max-w-md mx-auto">
        <div className="flex items-center gap-3 mb-8">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-fin-blue text-white shadow-lg shadow-blue-500/25">
            <Zap className="w-5 h-5" />
          </div>
          <div>
            <h1 className="text-lg font-bold tracking-tight text-white leading-tight">FINEDGE</h1>
            <p className="text-xs font-mono text-fin-blue font-semibold uppercase">
              Indian Portfolio Risk &amp; Analytics
            </p>
          </div>
        </div>

        <div className="mb-6">
          <h2 className="text-2xl font-bold text-white tracking-tight">Create Analyst Account</h2>
          <p className="text-xs text-fin-muted mt-1.5">
            Set up your credentials to begin tracking and optimizing multi-asset portfolios
          </p>
        </div>

        {error && (
          <div className="mb-5 p-3 rounded-lg bg-red-500/10 border border-red-500/20 text-xs text-fin-red">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="Full Name"
            type="text"
            placeholder="John Doe"
            leftIcon={<User className="w-4 h-4" />}
            error={errors.fullName?.message}
            {...register('fullName')}
          />

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

          <Input
            label="Confirm Password"
            type="password"
            placeholder="••••••••"
            leftIcon={<Lock className="w-4 h-4" />}
            error={errors.confirmPassword?.message}
            {...register('confirmPassword')}
          />

          <Button
            type="submit"
            variant="primary"
            className="w-full mt-2"
            isLoading={isLoading}
            rightIcon={<ArrowRight className="w-4 h-4" />}
          >
            Create Account
          </Button>
        </form>

        <div className="mt-6 text-center text-xs text-fin-muted">
          Already registered?{' '}
          <Link to="/login" className="text-fin-blue hover:underline font-medium">
            Sign in
          </Link>
        </div>
      </div>
    </div>
  );
};
