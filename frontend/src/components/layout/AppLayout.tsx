import React from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { TopHeader } from './TopHeader';

export const AppLayout: React.FC = () => {
  return (
    <div className="flex h-screen w-screen overflow-hidden bg-fin-bg text-fin-text antialiased">
      {/* Collapsible Sidebar */}
      <Sidebar />

      {/* Main Content Area */}
      <div className="flex flex-1 flex-col overflow-hidden min-w-0">
        {/* Top Header */}
        <TopHeader />

        {/* Dynamic Page Content */}
        <main className="flex-1 overflow-y-auto px-4 py-6 md:px-8 max-w-[1600px] w-full mx-auto scrollbar-thin">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
