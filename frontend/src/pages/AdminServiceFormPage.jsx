import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { serviceApi } from '../api/serviceApi';
import { parseApiError } from '../api/httpClient';
import Loader from '../components/Loader';
import ErrorAlert from '../components/ErrorAlert';

const createSchema = z.object({
  code: z.string().min(2, 'Code is required'),
  name: z.string().min(2, 'Name is required'),
  description: z.string().optional(),
  price: z.coerce.number().min(0, 'Price must be 0 or more'),
  active: z.boolean()
});

const editSchema = z.object({
  name: z.string().min(2, 'Name is required'),
  description: z.string().optional(),
  price: z.coerce.number().min(0, 'Price must be 0 or more'),
  active: z.boolean()
});

export default function AdminServiceFormPage({ mode }) {
  const isEdit = mode === 'edit';
  const { id } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [formError, setFormError] = useState('');

  const serviceQuery = useQuery({
    queryKey: ['admin-service-detail', id],
    queryFn: () => serviceApi.getById(id),
    enabled: isEdit && Boolean(id)
  });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting }
  } = useForm({
    resolver: zodResolver(isEdit ? editSchema : createSchema),
    defaultValues: {
      code: '',
      name: '',
      description: '',
      price: 0,
      active: true
    }
  });

  useEffect(() => {
    if (isEdit && serviceQuery.data) {
      reset({
        code: serviceQuery.data.code || '',
        name: serviceQuery.data.name || '',
        description: serviceQuery.data.description || '',
        price: Number(serviceQuery.data.price || 0),
        active: Boolean(serviceQuery.data.active)
      });
    }
  }, [isEdit, serviceQuery.data, reset]);

  const mutation = useMutation({
    mutationFn: async (values) => {
      if (isEdit) {
        return serviceApi.update(id, {
          name: values.name,
          description: values.description || '',
          price: String(values.price),
          active: values.active
        });
      }

      return serviceApi.create({
        code: values.code,
        name: values.name,
        description: values.description || '',
        price: Number(values.price),
        active: values.active
      });
    },
    onSuccess: () => {
      setFormError('');
      queryClient.invalidateQueries({ queryKey: ['admin-services'] });
      queryClient.invalidateQueries({ queryKey: ['services'] });
      navigate('/admin/services', { replace: true });
    },
    onError: (error) => {
      setFormError(parseApiError(error));
    }
  });

  if (isEdit && serviceQuery.isLoading) {
    return <Loader label="Loading service..." />;
  }

  const detailError = serviceQuery.error ? parseApiError(serviceQuery.error) : '';
  const actionError = formError || detailError;

  const onSubmit = async (values) => {
    setFormError('');
    await mutation.mutateAsync(values);
  };

  return (
    <section className="page-card narrow">
      <div className="row-spread">
        <h1>{isEdit ? 'Edit Service' : 'Create Service'}</h1>
        <Link to="/admin/services" className="btn secondary">
          Back
        </Link>
      </div>

      <ErrorAlert message={actionError} />

      {!detailError ? (
        <form className="form-grid" onSubmit={handleSubmit(onSubmit)}>
          {!isEdit ? (
            <label>
              Code
              <input type="text" placeholder="GST-FILING" {...register('code')} />
              {errors.code ? <span className="field-error">{errors.code.message}</span> : null}
            </label>
          ) : null}

          <label>
            Name
            <input type="text" placeholder="Service name" {...register('name')} />
            {errors.name ? <span className="field-error">{errors.name.message}</span> : null}
          </label>

          <label>
            Description
            <textarea rows={4} placeholder="Service details" {...register('description')} />
          </label>

          <label>
            Price
            <input type="number" min="0" step="0.01" {...register('price')} />
            {errors.price ? <span className="field-error">{errors.price.message}</span> : null}
          </label>

          <label className="checkbox-row">
            <input type="checkbox" {...register('active')} />
            Active
          </label>

          <button type="submit" className="btn" disabled={isSubmitting || mutation.isPending}>
            {isSubmitting || mutation.isPending ? 'Saving...' : isEdit ? 'Update Service' : 'Create Service'}
          </button>
        </form>
      ) : null}
    </section>
  );
}
