import { Filter, Search } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { AgendaCategory, AgendaStatus } from '../types/agenda'
import { Label } from '@/components/ui/label'

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
          <Select value={statusFilter} onValueChange={onStatusChange}>
            <SelectTrigger className="w-full">
              <SelectValue placeholder="Todos os status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">Todos os status</SelectItem>
              <SelectItem value="não_iniciada">Não iniciadas</SelectItem>
              <SelectItem value="em_andamento">Em andamento</SelectItem>
              <SelectItem value="encerrada">Encerradas</SelectItem>
              <SelectItem value="cancelada">Canceladas</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Filtro por categoria */}
        <div className="space-y-2 w-full">
          <Label className="text-sm text-gray-400">Categoria</Label>
          <Select value={categoryFilter} onValueChange={onCategoryChange} >
            <SelectTrigger className="w-full">
              <SelectValue placeholder="Todas as categorias" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">Todas as categorias</SelectItem>
              <SelectItem value="Projetos">Projetos</SelectItem>
              <SelectItem value="Administrativo">Administrativo</SelectItem>
              <SelectItem value="Eleições">Eleições</SelectItem>
              <SelectItem value="Estatutário">Estatutário</SelectItem>
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

        {(statusFilter !== "ALL" || categoryFilter !== "ALL" || searchTerm !== "") && (
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