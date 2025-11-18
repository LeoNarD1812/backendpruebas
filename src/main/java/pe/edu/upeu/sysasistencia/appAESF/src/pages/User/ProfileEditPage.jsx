import React, { useState, useEffect } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { authService } from '../../services/authService';
import { personaService } from '../../services/personaService';
import { getRoleFromToken } from '../../utils/jwtHelper';
import { TipoPersona } from '../../utils/constants.js';

const ProfileEditPage = () => {
    const { user, isAuthenticated } = useAuth();
    const [profileData, setProfileData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);

    useEffect(() => {
        const fetchProfile = async () => {
            setLoading(true);
            setError(null);
            setSuccessMessage(null);
            try {
                const token = authService.getToken();
                if (!token) {
                    throw new Error("No autenticado");
                }
                const data = await personaService.getMyProfile(token);
                setProfileData(data);
            } catch (err) {
                console.error("Error al cargar el perfil:", err);
                // Añadir log detallado del error de Axios
                if (err.response) {
                    console.error("Detalles del error (err.response):", err.response);
                    console.error("Datos del error (err.response.data):", err.response.data);
                    console.error("Estado del error (err.response.status):", err.response.status);
                    console.error("Encabezados del error (err.response.headers):", err.response.headers);
                } else if (err.request) {
                    console.error("Detalles del error (err.request):", err.request);
                } else {
                    console.error("Mensaje de error:", err.message);
                }
                setError("Error al cargar el perfil: " + (err.message || ""));
            } finally {
                setLoading(false);
            }
        };

        if (isAuthenticated()) {
            fetchProfile();
        }
    }, [isAuthenticated]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setProfileData(prevData => ({
            ...prevData,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccessMessage(null);
        try {
            const token = authService.getToken();
            if (!token) {
                throw new Error("No autenticado");
            }
            const dataToSend = { ...profileData };
            delete dataToSend.tipoPersona;
            delete dataToSend.usuario;

            const updatedProfile = await personaService.updateMyProfile(dataToSend, token);
            setProfileData(updatedProfile);
            setSuccessMessage("Perfil actualizado exitosamente!");
        } catch (err) {
            console.error("Error al actualizar el perfil:", err);
            // Añadir log detallado del error de Axios para el update también
            if (err.response) {
                console.error("Detalles del error (err.response):", err.response);
                console.error("Datos del error (err.response.data):", err.response.data);
                console.error("Estado del error (err.response.status):", err.response.status);
                console.error("Encabezados del error (err.response.headers):", err.response.headers);
            } else if (err.request) {
                console.error("Detalles del error (err.request):", err.request);
            } else {
                console.error("Mensaje de error:", err.message);
            }
            setError("Error al actualizar el perfil: " + (err.message || ""));
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="p-4">Cargando perfil...</div>;
    }

    if (error) {
        return <div className="p-4 text-red-500">Error: {error}</div>;
    }

    if (!profileData) {
        return <div className="p-4">No se pudo cargar la información del perfil.</div>;
    }

    const isInvitado = profileData.tipoPersona === TipoPersona.INVITADO;

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4">Editar Perfil</h1>
            {successMessage && <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4">{successMessage}</div>}
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label htmlFor="nombreCompleto" className="block text-sm font-medium text-gray-700">Nombre Completo</label>
                    <input
                        type="text"
                        name="nombreCompleto"
                        id="nombreCompleto"
                        value={profileData.nombreCompleto || ''}
                        onChange={handleChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                        required
                    />
                </div>
                <div>
                    <label htmlFor="documento" className="block text-sm font-medium text-gray-700">Documento</label>
                    <input
                        type="text"
                        name="documento"
                        id="documento"
                        value={profileData.documento || ''}
                        onChange={handleChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                        required
                    />
                </div>
                <div>
                    <label htmlFor="correo" className="block text-sm font-medium text-gray-700">Correo Personal</label>
                    <input
                        type="email"
                        name="correo"
                        id="correo"
                        value={profileData.correo || ''}
                        onChange={handleChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                    />
                </div>
                <div>
                    <label htmlFor="correoInstitucional" className="block text-sm font-medium text-gray-700">Correo Institucional</label>
                    <input
                        type="email"
                        name="correoInstitucional"
                        id="correoInstitucional"
                        value={profileData.correoInstitucional || ''}
                        onChange={handleChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2 bg-gray-100"
                        disabled={isInvitado}
                    />
                    {isInvitado && <p className="mt-1 text-sm text-gray-500">Los invitados no pueden editar este campo.</p>}
                </div>
                <div>
                    <label htmlFor="codigoEstudiante" className="block text-sm font-medium text-gray-700">Código Estudiante</label>
                    <input
                        type="text"
                        name="codigoEstudiante"
                        id="codigoEstudiante"
                        value={profileData.codigoEstudiante || ''}
                        onChange={handleChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2 bg-gray-100"
                        disabled={isInvitado}
                    />
                    {isInvitado && <p className="mt-1 text-sm text-gray-500">Los invitados no pueden editar este campo.</p>}
                </div>
                <div>
                    <label htmlFor="celular" className="block text-sm font-medium text-gray-700">Celular</label>
                    <input
                        type="text"
                        name="celular"
                        id="celular"
                        value={profileData.celular || ''}
                        onChange={handleChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                    />
                </div>
                <div>
                    <label htmlFor="pais" className="block text-sm font-medium text-gray-700">País</label>
                    <input
                        type="text"
                        name="pais"
                        id="pais"
                        value={profileData.pais || ''}
                        onChange={handleChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                    />
                </div>
                <div>
                    <label htmlFor="religion" className="block text-sm font-medium text-gray-700">Religión</label>
                    <input
                        type="text"
                        name="religion"
                        id="religion"
                        value={profileData.religion || ''}
                        onChange={handleChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                    />
                </div>
                <div>
                    <label htmlFor="fechaNacimiento" className="block text-sm font-medium text-gray-700">Fecha de Nacimiento</label>
                    <input
                        type="date"
                        name="fechaNacimiento"
                        id="fechaNacimiento"
                        value={profileData.fechaNacimiento || ''}
                        onChange={handleChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm p-2"
                    />
                </div>
                <button
                    type="submit"
                    className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >
                    Guardar Cambios
                </button>
            </form>
        </div>
    );
};

export default ProfileEditPage;
