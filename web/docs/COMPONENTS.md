# Componentes

## 1. Componentes de UI

### Button
```tsx
interface ButtonProps {
  variant: 'primary' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
  disabled?: boolean;
  children: React.ReactNode;
  onClick?: () => void;
}

export const Button: React.FC<ButtonProps> = ({
  variant,
  size = 'md',
  isLoading,
  disabled,
  children,
  onClick
}) => {
  return (
    <button
      className={clsx(
        'rounded-lg font-medium transition-colors',
        {
          'bg-primary text-white hover:bg-primary-dark': variant === 'primary',
          'bg-gray-200 text-gray-800 hover:bg-gray-300': variant === 'secondary',
          'bg-red-500 text-white hover:bg-red-600': variant === 'danger',
          'opacity-50 cursor-not-allowed': disabled || isLoading,
          'px-3 py-1 text-sm': size === 'sm',
          'px-4 py-2': size === 'md',
          'px-6 py-3 text-lg': size === 'lg'
        }
      )}
      disabled={disabled || isLoading}
      onClick={onClick}
    >
      {isLoading ? <Spinner /> : children}
    </button>
  );
};
```

### Card
```tsx
interface CardProps {
  title?: string;
  children: React.ReactNode;
  className?: string;
}

export const Card: React.FC<CardProps> = ({
  title,
  children,
  className
}) => {
  return (
    <div className={clsx(
      'bg-white rounded-lg shadow-md p-4',
      className
    )}>
      {title && (
        <h3 className="text-lg font-semibold mb-4">{title}</h3>
      )}
      {children}
    </div>
  );
};
```

## 2. Componentes de Formulário

### Input
```tsx
interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

export const Input: React.FC<InputProps> = ({
  label,
  error,
  ...props
}) => {
  return (
    <div className="flex flex-col gap-1">
      {label && (
        <label className="text-sm font-medium text-gray-700">
          {label}
        </label>
      )}
      <input
        className={clsx(
          'rounded-lg border px-3 py-2',
          {
            'border-red-500 focus:ring-red-500': error,
            'border-gray-300 focus:ring-primary': !error
          }
        )}
        {...props}
      />
      {error && (
        <span className="text-sm text-red-500">{error}</span>
      )}
    </div>
  );
};
```

### Select
```tsx
interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  error?: string;
  options: Array<{
    value: string;
    label: string;
  }>;
}

export const Select: React.FC<SelectProps> = ({
  label,
  error,
  options,
  ...props
}) => {
  return (
    <div className="flex flex-col gap-1">
      {label && (
        <label className="text-sm font-medium text-gray-700">
          {label}
        </label>
      )}
      <select
        className={clsx(
          'rounded-lg border px-3 py-2',
          {
            'border-red-500 focus:ring-red-500': error,
            'border-gray-300 focus:ring-primary': !error
          }
        )}
        {...props}
      >
        {options.map(option => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      {error && (
        <span className="text-sm text-red-500">{error}</span>
      )}
    </div>
  );
};
```

## 3. Componentes de Layout

### Container
```tsx
interface ContainerProps {
  children: React.ReactNode;
  className?: string;
}

export const Container: React.FC<ContainerProps> = ({
  children,
  className
}) => {
  return (
    <div className={clsx(
      'max-w-7xl mx-auto px-4 sm:px-6 lg:px-8',
      className
    )}>
      {children}
    </div>
  );
};
```

### Grid
```tsx
interface GridProps {
  children: React.ReactNode;
  cols?: 1 | 2 | 3 | 4;
  gap?: 2 | 4 | 6 | 8;
  className?: string;
}

export const Grid: React.FC<GridProps> = ({
  children,
  cols = 3,
  gap = 4,
  className
}) => {
  return (
    <div className={clsx(
      'grid',
      {
        'grid-cols-1': cols === 1,
        'grid-cols-2': cols === 2,
        'grid-cols-3': cols === 3,
        'grid-cols-4': cols === 4,
        'gap-2': gap === 2,
        'gap-4': gap === 4,
        'gap-6': gap === 6,
        'gap-8': gap === 8
      },
      className
    )}>
      {children}
    </div>
  );
};
```

## 4. Componentes de Feedback

### Alert
```tsx
interface AlertProps {
  type: 'success' | 'error' | 'warning' | 'info';
  title?: string;
  children: React.ReactNode;
}

export const Alert: React.FC<AlertProps> = ({
  type,
  title,
  children
}) => {
  return (
    <div className={clsx(
      'rounded-lg p-4',
      {
        'bg-green-50 text-green-800': type === 'success',
        'bg-red-50 text-red-800': type === 'error',
        'bg-yellow-50 text-yellow-800': type === 'warning',
        'bg-blue-50 text-blue-800': type === 'info'
      }
    )}>
      {title && (
        <h4 className="text-lg font-semibold mb-2">{title}</h4>
      )}
      {children}
    </div>
  );
};
```

### Spinner
```tsx
interface SpinnerProps {
  size?: 'sm' | 'md' | 'lg';
}

export const Spinner: React.FC<SpinnerProps> = ({
  size = 'md'
}) => {
  return (
    <div className={clsx(
      'animate-spin rounded-full border-2 border-current border-t-transparent',
      {
        'h-4 w-4': size === 'sm',
        'h-6 w-6': size === 'md',
        'h-8 w-8': size === 'lg'
      }
    )} />
  );
};
```

## 5. Componentes de Navegação

### Breadcrumb
```tsx
interface BreadcrumbProps {
  items: Array<{
    label: string;
    href?: string;
  }>;
}

export const Breadcrumb: React.FC<BreadcrumbProps> = ({
  items
}) => {
  return (
    <nav className="flex" aria-label="Breadcrumb">
      <ol className="flex items-center space-x-2">
        {items.map((item, index) => (
          <li key={item.label}>
            {index > 0 && (
              <span className="mx-2 text-gray-400">/</span>
            )}
            {item.href ? (
              <Link
                to={item.href}
                className="text-gray-500 hover:text-gray-700"
              >
                {item.label}
              </Link>
            ) : (
              <span className="text-gray-900">{item.label}</span>
            )}
          </li>
        ))}
      </ol>
    </nav>
  );
};
```

## 6. Componentes de Modal

### Modal
```tsx
interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode;
}

export const Modal: React.FC<ModalProps> = ({
  isOpen,
  onClose,
  title,
  children
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div
        className="fixed inset-0 bg-black bg-opacity-50"
        onClick={onClose}
      />
      <div className="relative bg-white rounded-lg p-6 max-w-lg w-full">
        {title && (
          <h2 className="text-xl font-semibold mb-4">{title}</h2>
        )}
        {children}
      </div>
    </div>
  );
};
```

## 7. Componentes de Tabela

### Table
```tsx
interface TableProps<T> {
  data: T[];
  columns: Array<{
    key: keyof T;
    header: string;
    render?: (value: T[keyof T], item: T) => React.ReactNode;
  }>;
}

export const Table = <T extends Record<string, any>>({
  data,
  columns
}: TableProps<T>) => {
  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            {columns.map(column => (
              <th
                key={String(column.key)}
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                {column.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {data.map((item, index) => (
            <tr key={index}>
              {columns.map(column => (
                <td
                  key={String(column.key)}
                  className="px-6 py-4 whitespace-nowrap"
                >
                  {column.render
                    ? column.render(item[column.key], item)
                    : item[column.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
``` 