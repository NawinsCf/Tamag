"use client";

import React, { useState } from 'react';

type Gauge = {
  key?: string;
  label: string;
  value: number;
  max?: number;
  color?: string; // optional custom color
};

type CtamagProps = {
  label: string;
  imageSrc?: string;
  /** Optional full tamagoType object retrieved from the API. If provided
   *  the PV and PF gauges will be derived from it. */
  tamagoType?: {
    PV?: number | null;
    PF?: number | null;
    pv?: number | null;
    pf?: number | null;
    nomImg?: string | null; // filename stored in DB (server-generated)
    image?: string | null; // alternative image field
  } | null;
  /** Optional tamago instance (living tamago) when rendering a user's tamago */
  tamago?: {
    pv?: number | null;
    pf?: number | null;
    PV?: number | null;
    PF?: number | null;
    id?: number | string;
    nom?: string | null;
  } | null;
  imageAlt?: string;
  gauges?: Gauge[]; // left = gauges[0], right = gauges[1]
  size?: 'sm' | 'md' | 'lg';
  className?: string;
  onClick?: () => void;
  /** Optional simple dual-value labels shown instead of the two gauges. The slash is fixed and not passed.
   * Example: dualLabelLeft="12" dualLabelRight="34" or dualLabelLeft="PV" dualLabelRight="PF" */
  dualLabelLeft?: string;
  dualLabelRight?: string;
};

// clamp was used by the previous gauge renderer; removed because it's unused to satisfy ESLint.

export default function Ctamag({ label, imageSrc, imageAlt, tamagoType, tamago, size = 'md', className = '', onClick }: CtamagProps) {
  // If a tamagoType was passed in props, prefer computing the two gauges from it.
  // Note: per your request both current and max values for PV should equal tamagoType.PV
  // (same for PF). If PV/PF are missing we'll fall back to the gauges prop.
  const [imgFailed, setImgFailed] = useState(false);

  // Title size mapping only; image height is fixed to 256px (h-64)
  const sizeMap: Record<string, { title: string }> = {
    sm: { title: 'text-sm' },
    md: { title: 'text-base' },
    lg: { title: 'text-lg' },
  };

  const { title } = sizeMap[size] || sizeMap.md;

  // width mapping so a standalone card doesn't stretch full width
  const widthMap: Record<string, string> = {
    sm: 'max-w-xs',
    md: 'max-w-sm',
    lg: 'max-w-md',
  };
  const widthClass = widthMap[size] || widthMap.md;

  // The previous gauge rendering code was removed: Ctamag now renders
  // a structured PV/PF display at the bottom of the card.

  const cardBorder = 'border-4 border-orange-400';

  // Resolve image source: prefer explicit `imageSrc`, then tamagoType.nomImg / image,
  // and finally fall back to Default.jpg. If the returned name looks like a bare
  // filename (no slash) prefix it with the public upload folder used by the app.
  const resolveImageSrc = (raw?: string | null | undefined) => {
    if (!raw) return '/tamagoimage/Default.jpg';
    // If looks like a filename only (no slash), prefix with tamagoimage folder
    if (!raw.includes('/')) return `/tamagoimage/${raw}`;
    return raw;
  };

  // pick an image from explicit imageSrc or tamagoType.nomImg / tamagoType.image
  const finalImageRaw = imgFailed ? '/tamagoimage/Default.jpg' : (imageSrc || (tamagoType && (tamagoType.nomImg || tamagoType.image)) || null);
  const finalImage = resolveImageSrc(finalImageRaw as string | null | undefined);
  // Compute PV/PF display parameters according to the rules:
  // - If a tamago instance is provided: paramPV1 = tamago.pv, paramPF1 = tamago.pf
  //   and paramPV2/paramPF2 come from the tamagoType (if available).
  // - If only tamagoType is provided (we're rendering a tamagotype card): both params equal tamagoType pv/PV and pf/PF.
  const toNumberOrNull = (v: number | null | undefined) => (v === null || v === undefined || Number.isNaN(v) ? null : Number(v));

  const tamPv = toNumberOrNull((tamago && (tamago.pv ?? tamago.PV)) ?? undefined);
  const tamPf = toNumberOrNull((tamago && (tamago.pf ?? tamago.PF)) ?? undefined);
  const typePv = toNumberOrNull(tamagoType?.PV ?? tamagoType?.pv);
  const typePf = toNumberOrNull(tamagoType?.PF ?? tamagoType?.pf);

  let paramPV1: number | null = null;
  let paramPV2: number | null = null;
  let paramPF1: number | null = null;
  let paramPF2: number | null = null;

  if (tamago) {
    paramPV1 = tamPv ?? null;
    paramPF1 = tamPf ?? null;
    paramPV2 = typePv ?? paramPV1;
    paramPF2 = typePf ?? paramPF1;
  } else if (tamagoType) {
    paramPV1 = typePv ?? null;
    paramPV2 = typePv ?? paramPV1;
    paramPF1 = typePf ?? null;
    paramPF2 = typePf ?? paramPF1;
  }

  const formatVal = (v: number | null) => (v === null || v === undefined ? '-' : `${v}`);

  return (
    <div
      className={`card-contenaire bg-white ${cardBorder} rounded-2xl shadow-lg overflow-hidden ${widthClass} mx-auto ${className} ${onClick ? 'cursor-pointer focus:outline-none focus:ring-2 focus:ring-orange-300' : ''}`}
      style={{ maxHeight: '420px' }}
      onClick={onClick}
      tabIndex={onClick ? 0 : -1}
      aria-label={label}
    >
      {/* top: title container */}
      <div className={`cardtitle-contenaire px-4 pt-4 pb-2 ${title}`}>
        <div className="text-center font-extrabold tracking-tight text-orange-700 drop-shadow-sm">{label}</div>
      </div>

      {/* center: image container */}
      <div className={`cardimage-contenaire w-full h-64 bg-gray-100 flex items-center justify-center overflow-hidden`}>
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img
          src={finalImage}
          alt={imageAlt || label}
          className="max-w-full max-h-full object-contain"
          onError={() => {
            if (!imgFailed) setImgFailed(true);
          }}
        />
      </div>

      {/* bottom: two side-by-side containers */}
      <div className="cardbottom-contenaire px-4 py-3">
        <div className="flex gap-3 items-center justify-between">
          <div className="w-1/2 text-center">
            <div className="text-sm font-semibold text-gray-700">PV = {formatVal(paramPV1)}/{formatVal(paramPV2)}</div>
          </div>
          <div className="w-1/2 text-center">
            <div className="text-sm font-semibold text-gray-700">PF = {formatVal(paramPF1)}/{formatVal(paramPF2)}</div>
          </div>
        </div>
      </div>
    </div>
  );
}
