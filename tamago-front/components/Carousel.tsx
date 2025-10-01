"use client";

import React, { useCallback, useEffect, useState } from 'react';
import useEmblaCarousel from 'embla-carousel-react';
// import Ctamag from './Ctamag'; // unused here

type CarouselProps = {
  children: React.ReactNode[];
  loop?: boolean;
  autoplay?: number | false; // ms or false
};

export default function Carousel({ children, loop = false, autoplay = false }: CarouselProps) {
  const [viewportRef, embla] = useEmblaCarousel({ align: 'center', containScroll: 'trimSnaps', loop });
  const [selectedIndex, setSelectedIndex] = useState(0);

  const onSelect = useCallback(() => {
    if (!embla) return;
    setSelectedIndex(embla.selectedScrollSnap());
  }, [embla]);

  useEffect(() => {
    if (!embla) return;
    embla.on('select', onSelect);
    onSelect();
  }, [embla, onSelect]);

  // autoplay
  useEffect(() => {
    if (!embla || !autoplay) return;
    const timer = setInterval(() => {
      if (embla) embla.scrollNext();
    }, autoplay as number);
    return () => { clearInterval(timer); };
  }, [embla, autoplay]);

  const scrollPrev = useCallback(() => { if (embla) embla.scrollPrev(); }, [embla]);
  const scrollNext = useCallback(() => { if (embla) embla.scrollNext(); }, [embla]);

  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (!embla) return;
      if (e.key === 'ArrowLeft') embla.scrollPrev();
      if (e.key === 'ArrowRight') embla.scrollNext();
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [embla]);

  return (
    <div className="relative overflow-visible" style={{ perspective: 1000 }}>
      <div className="overflow-hidden" ref={viewportRef}>
        <div className="flex gap-10 px-6 items-center">
        {children.map((child, idx) => {
            // determine transform based on position relative to selected index
            let transformStyle = '';
            if (selectedIndex === idx) {
              transformStyle = 'scale(1.02) rotateY(0deg) translateX(0px)';
            } else if (idx < selectedIndex) {
              transformStyle = 'scale(0.96) rotateY(-6deg) translateX(-6px)';
            } else {
              transformStyle = 'scale(0.96) rotateY(6deg) translateX(6px)';
            }

            return (
              <div key={idx} className={`flex-[0_0_auto] w-[260px] md:w-[360px] lg:w-[420px] transition-transform duration-500`} style={{ transformOrigin: 'center' }}>
                <div className={`transition-all duration-500`} style={{ transform: transformStyle, transformStyle: 'preserve-3d' }}>
                  {child}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <button
        aria-label="Previous"
        onClick={scrollPrev}
        className="absolute left-2 top-1/2 -translate-y-1/2 z-50 bg-white/90 rounded-full w-10 h-10 flex items-center justify-center text-lg shadow-lg hover:bg-white focus:outline-none focus:ring-2 focus:ring-orange-300 pointer-events-auto"
      >
        ◀
      </button>
      <button
        aria-label="Next"
        onClick={scrollNext}
        className="absolute right-2 top-1/2 -translate-y-1/2 z-50 bg-white/90 rounded-full w-10 h-10 flex items-center justify-center text-lg shadow-lg hover:bg-white focus:outline-none focus:ring-2 focus:ring-orange-300 pointer-events-auto"
      >
        ▶
      </button>

      <div className="flex justify-center gap-2 mt-3">
        {children.map((_, i) => (
          <button key={i} onClick={() => embla && embla.scrollTo(i)} className={`w-2 h-2 rounded-full ${selectedIndex === i ? 'bg-orange-500' : 'bg-gray-300'}`} aria-label={`Go to slide ${i+1}`} />
        ))}
      </div>
    </div>
  );
}
