import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Briefcase,
  Plus,
  ArrowRight,
  Trash2,
  CheckCircle,
  ExternalLink,
  Layers,
  Calendar,
} from 'lucide-react';
import { portfolioApi } from '../../api/portfolioApi';
import { usePortfolioStore } from '../../stores/portfolioStore';
import { Card } from '../../components/common/Card';
import { Button } from '../../components/common/Button';
import { Badge } from '../../components/common/Badge';
import { Modal } from '../../components/common/Modal';
import { Input } from '../../components/common/Input';
import { Select } from '../../components/common/Select';
import { EmptyState } from '../../components/common/EmptyState';
import { Skeleton } from '../../components/common/Skeleton';

const createPortfolioSchema = z.object({
  name: z.string().min(2, 'Portfolio name must be at least 2 characters'),
  description: z.string().optional(),
  baseCurrency: z.string().min(1),
  benchmarkSymbol: z.string().min(1),
});

type CreatePortfolioFormData = z.infer<typeof createPortfolioSchema>;

export const PortfoliosPage: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { selectedPortfolioId, setSelectedPortfolioId } = usePortfolioStore();
  const [isCreateOpen, setIsCreateOpen] = useState(false);

  const { data: portfolios = [], isLoading } = useQuery({
    queryKey: ['portfolios'],
    queryFn: portfolioApi.getPortfolios,
  });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CreatePortfolioFormData>({
    resolver: zodResolver(createPortfolioSchema),
    defaultValues: {
      name: '',
      description: '',
      baseCurrency: 'INR',
      benchmarkSymbol: 'NIFTYBEES',
    },
  });

  const createMutation = useMutation({
    mutationFn: (data: CreatePortfolioFormData) => portfolioApi.createPortfolio(data),
    onSuccess: (newPortfolio) => {
      queryClient.invalidateQueries({ queryKey: ['portfolios'] });
      setSelectedPortfolioId(newPortfolio.id);
      setIsCreateOpen(false);
      reset();
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => portfolioApi.deletePortfolio(id),
    onSuccess: (_, deletedId) => {
      queryClient.invalidateQueries({ queryKey: ['portfolios'] });
      if (selectedPortfolioId === deletedId) {
        setSelectedPortfolioId(null);
      }
    },
  });

  const handleCreateSubmit = (data: CreatePortfolioFormData) => {
    createMutation.mutate(data);
  };

  const handleDelete = (e: React.MouseEvent, id: number) => {
    e.stopPropagation();
    if (confirm('Are you sure you want to delete this portfolio? This cannot be undone.')) {
      deleteMutation.mutate(id);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <Briefcase className="w-5 h-5 text-fin-blue" />
            Portfolios
          </h1>
          <p className="text-xs text-fin-muted mt-0.5">
            Manage your investment strategies, asset allocations, and trading books
          </p>
        </div>

        <Button
          variant="primary"
          size="sm"
          onClick={() => setIsCreateOpen(true)}
          leftIcon={<Plus className="w-4 h-4" />}
        >
          New Portfolio
        </Button>
      </div>

      {/* Educational Guide Banner */}
      <div className="p-4 rounded-xl bg-fin-card border border-fin-border flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 text-xs">
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-lg bg-fin-blue/10 flex items-center justify-center text-fin-blue font-bold">
            ₹
          </div>
          <div>
            <div className="font-semibold text-white">Indian Stock & ETF Portfolios</div>
            <div className="text-fin-muted">
              Holdings are denominated in INR (₹) and benchmarked against the Nifty 50 ETF (NIFTYBEES).
            </div>
          </div>
        </div>
        <div className="text-[11px] text-fin-muted font-mono bg-fin-panel px-3 py-1.5 rounded-lg border border-fin-border">
          Active Portfolios: {portfolios.length}
        </div>
      </div>

      {/* Grid of Portfolios */}
      {isLoading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <Skeleton height={200} />
          <Skeleton height={200} />
          <Skeleton height={200} />
        </div>
      ) : portfolios.length === 0 ? (
        <EmptyState
          icon={<Briefcase className="w-8 h-8 text-fin-blue" />}
          title="No Portfolios Created"
          description="Create your first investment portfolio to begin allocating assets and analyzing risk profiles."
          action={
            <Button
              variant="primary"
              onClick={() => setIsCreateOpen(true)}
              leftIcon={<Plus className="w-4 h-4" />}
            >
              Create Portfolio
            </Button>
          }
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {portfolios.map((portfolio) => {
            const isSelected = portfolio.id === selectedPortfolioId;
            return (
              <div
                key={portfolio.id}
                onClick={() => {
                  setSelectedPortfolioId(portfolio.id);
                  navigate(`/portfolios/${portfolio.id}`);
                }}
                className={`group relative bg-fin-card border rounded-xl p-5 shadow-card hover:border-fin-blue transition-all cursor-pointer flex flex-col justify-between ${
                  isSelected ? 'border-fin-blue/80 ring-1 ring-fin-blue/50' : 'border-fin-border'
                }`}
              >
                <div>
                  {/* Top row with status & active badge */}
                  <div className="flex items-center justify-between mb-2">
                    <Badge variant={isSelected ? 'success' : 'default'} size="sm">
                      {isSelected ? 'Active Portfolio' : 'Portfolio'}
                    </Badge>
                    <div className="flex items-center gap-1.5">
                      <Badge variant="info" size="sm">
                        {portfolio.baseCurrency || 'INR'} (₹)
                      </Badge>
                    </div>
                  </div>

                  <h3 className="text-base font-bold text-white group-hover:text-fin-blue transition-colors mt-2">
                    {portfolio.name}
                  </h3>
                  <p className="text-xs text-fin-muted mt-1.5 line-clamp-2">
                    {portfolio.description || 'Indian equity and multi-asset investment portfolio.'}
                  </p>
                </div>

                <div className="mt-6 pt-4 border-t border-fin-border flex items-center justify-between text-xs">
                  <div className="text-[11px] font-mono text-fin-muted">
                    Benchmark: <span className="text-white font-medium">{portfolio.benchmarkSymbol || 'NIFTYBEES'}</span>
                  </div>

                  <div className="flex items-center gap-2">
                    <button
                      onClick={(e) => handleDelete(e, portfolio.id)}
                      className="p-1.5 rounded text-fin-muted hover:text-fin-red hover:bg-fin-panel transition-colors"
                      title="Delete Portfolio"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                    <span className="text-xs font-semibold text-fin-blue group-hover:translate-x-1 transition-transform flex items-center gap-1">
                      Open <ArrowRight className="w-3.5 h-3.5" />
                    </span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Create Portfolio Modal */}
      <Modal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        title="Create New Portfolio"
        subtitle="Configure the base currency and benchmark for your portfolio"
      >
        <form onSubmit={handleSubmit(handleCreateSubmit)} className="space-y-4">
          <Input
            label="Portfolio Name"
            placeholder="e.g. India Bluechip Alpha Fund"
            error={errors.name?.message}
            {...register('name')}
          />

          <Input
            label="Description (Optional)"
            placeholder="Strategy notes, asset allocation targets, or mandate..."
            {...register('description')}
          />

          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Base Currency"
              options={[
                { value: 'INR', label: 'INR (₹) - Indian Rupee' },
                { value: 'USD', label: 'USD ($) - US Dollar' },
                { value: 'EUR', label: 'EUR (€) - Euro' },
                { value: 'GBP', label: 'GBP (£) - British Pound' },
              ]}
              {...register('baseCurrency')}
            />

            <Input
              label="Benchmark Symbol"
              placeholder="e.g. NIFTYBEES, GOLDBEES"
              {...register('benchmarkSymbol')}
            />
          </div>

          <div className="pt-4 flex items-center justify-end gap-3">
            <Button
              type="button"
              variant="outline"
              onClick={() => setIsCreateOpen(false)}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="primary"
              isLoading={createMutation.isPending}
            >
              Create Portfolio
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
