import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  initChatIndex,
  nowChatServerData,
} from '../../mocks/handler/chatHandler';

import { ReactNode } from 'react';
import { renderHook } from '@testing-library/react';
import useChats from './useChats';

const queryClient = new QueryClient();
const wrapper = ({ children }: { children: ReactNode }) => (
  <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
);

describe('useChats', () => {
  afterEach(() => {
    nowChatServerData.length = 0;
    initChatIndex();
  });
  it('초기의 chats은 빈 배열이다', () => {
    const { result } = renderHook(() => useChats(1), { wrapper });

    expect(result.current.chats).toHaveLength(0);
  });

  // TODO: 배포환경에서 예기치 못한 에러가 나옴
  // it('chats의 값은 서버의 값과 같다', async () => {
  //   const { result, rerender } = renderHook(() => useChats(1), { wrapper });
  //   for (let i = 0; i < chatSliceIndexes.length; i++) {
  //     await act(async () => {
  //       pushNextChatsIntoSever();
  //       await new Promise((res) => setInterval(res, 110));
  //       rerender();
  //     });
  //     expect(result.current.chats).toEqual(nowChatServerData);
  //   }
  // });
});
