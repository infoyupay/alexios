/*
 * Copyright (C) 2021 David Vidal Escudero - Free Open Source Software Consultancy
 *
 *       This program is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       This program is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
/**
 * Export feature for PDT #710 interop.
 * I should export the following fields:
 * <pre>
 *     361 -> X Cobrar Com. - Terceros
 *     362 -> X Cobrar Com. - Relacionadas
 *     364 -> X Cobrar Div. - Terceros
 *     365 -> X Cobrar Div. - Relacionadas
 *     367 -> X Cobrar dudosa
 *     404 -> X Pag. Com. - Terceros
 *     405 -> X Pag. Com. - Relacionadas
 *     407 -> X Pag. Diversas - Terceros
 *     408 -> X Pag. Diversas - Relacionadas
 * </pre>
 * This is not part of the PLE specification, but of PDT710,
 * tax return form of SUNAT. To see the full spec visit
 * <a href="https://renta.sunat.gob.pe/empresas/herramientas-para-la-declaracion-formulario-virtual-710">
 * SUNAT PDT 710 microsite</a>
 */
package org.dvidal.alexios.api.impl.pdt710;
