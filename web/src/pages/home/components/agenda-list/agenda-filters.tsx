import { Filter, Search } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Label } from '@/components/ui/label'
import {  AgendaCategory } from '@/shared/types/agenda'
import { AgendaStatus } from '@/shared/types/agenda-status'

interface AgendaFiltersProps {
  statusFilter: AgendaStatus
  categoryFilter: AgendaCategory
  searchTerm: string
  totalAgendas: number
  onStatusChange: (value: AgendaStatus) => void
  onCategoryChange: (value: AgendaCategory) => void
  onSearchChange: (value: string) => void
  onClearFilters: () => void
}

/**
 * Componente de filtros para a lista de agendas
 * @param props - Propriedades do componente
 * @returns Componente de filtros
 */
export function AgendaFilters({
  statusFilter,
  categoryFilter,
  searchTerm,
  totalAgendas,
  onStatusChange,
  onCategoryChange,
  onSearchChange,
  onClearFilters,
}: AgendaFiltersProps) {
  return (
    <div className="p-4 rounded-lg border">
      <div className="flex items-center gap-2 mb-3">
        <Filter className="h-5 w-5 text-gray-400" />
        <h3 className="font-medium">Filtros</h3>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Filtro por status */}
        <div className="w-full space-y-2">
          <Label className="text-sm text-gray-400">Status</Label>
          <Select value={statusFilter} onValueChange={(value) => onStatusChange(value as AgendaStatus)}>
            <SelectTrigger className="w-full">
              <SelectValue placeholder="Todos os status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value={AgendaStatus.ALL}>Todos os status</SelectItem>
              <SelectItem value={AgendaStatus.DRAFT}>Rascunho</SelectItem>
              <SelectItem value={AgendaStatus.OPEN}>Não iniciadas</SelectItem>
              <SelectItem value={AgendaStatus.IN_PROGRESS}>Em andamento</SelectItem>
              <SelectItem value={AgendaStatus.FINISHED}>Encerradas</SelectItem>
              <SelectItem value={AgendaStatus.CANCELLED}>Canceladas</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Filtro por categoria */}
        <div className="space-y-2 w-full">
          <Label className="text-sm text-gray-400">Categoria</Label>
          <Select value={categoryFilter} onValueChange={(value) => onCategoryChange(value as AgendaCategory)}>
            <SelectTrigger className="w-full">
              <SelectValue placeholder="Todas as categorias" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value={AgendaCategory.ALL}>Todas as categorias</SelectItem>
              <SelectItem value={AgendaCategory.PROJETOS}>Projetos</SelectItem>
              <SelectItem value={AgendaCategory.ADMINISTRATIVO}>Administrativo</SelectItem>
              <SelectItem value={AgendaCategory.ELEICOES}>Eleições</SelectItem>
              <SelectItem value={AgendaCategory.ESTATUTARIO}>Estatutário</SelectItem>
              <SelectItem value={AgendaCategory.FINANCEIRO}>Financeiro</SelectItem>
              <SelectItem value={AgendaCategory.OUTROS}>Outros</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Busca por texto */}
        <div className="space-y-2 w-full">
          <Label className="text-sm text-gray-400">Buscar</Label>
          <div className="relative">
            <Input
              className="pl-10"
              placeholder="Buscar por título ou descrição"
              value={searchTerm}
              onChange={(e) => onSearchChange(e.target.value)}
            />
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
          </div>
        </div>
      </div>

      {/* Contador de resultados e botão para limpar filtros */}
      <div className="flex justify-between items-center mt-4">
        <p className="text-sm text-gray-400">
          {totalAgendas} {totalAgendas === 1 ? "pauta encontrada" : "pautas encontradas"}
        </p>

        {(statusFilter !== AgendaStatus.ALL || categoryFilter !== AgendaCategory.ALL || searchTerm !== "") && (
          <Button
            variant="ghost"
            size="sm"
            onClick={onClearFilters}
          >
            Limpar filtros
          </Button>
        )}
      </div>
    </div>
  )
} 