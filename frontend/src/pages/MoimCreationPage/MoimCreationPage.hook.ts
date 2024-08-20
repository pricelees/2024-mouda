import useStatePersist from '@_hooks/useStatePersist';
import { MoimInputInfo } from '@_types/index';
import {
  isApprochedByUrl,
  validateDate,
  validateMaxPeople,
  validatePlace,
  validateTime,
  validateTitle,
} from './MoimCreatePage.util';
import { useEffect, useState } from 'react';
import useAddMoim from '@_hooks/mutaions/useAddMoim';
import { useNavigate, useNavigationType } from 'react-router-dom';
import { MoimCreationStep } from './MoimCreationPage';

type MoimFormData = MoimInputInfo & {
  // offlineOrOnline: string;
  description: string;
};

const useMoimCreationForm = (currentStep: MoimCreationStep) => {
  const navigationType = useNavigationType();
  const navigate = useNavigate();

  const isNewMoimCreation =
    currentStep === '이름입력' &&
    (navigationType === 'PUSH' || isApprochedByUrl());

  if (isNewMoimCreation) {
    sessionStorage.removeItem('moimCreationInfo');
  }

  const [formData, setFormData] = useStatePersist<MoimFormData>({
    key: 'moimCreationInfo',
    initialState: {
      title: '',
      // offlineOrOnline: '',
      place: '',
      date: '',
      time: '',
      maxPeople: 0,
      description: '',
    },
    storage: 'sessionStorage',
  });

  const [formValidation, setFormValidation] = useState({
    title: false,
    place: false,
    date: false,
    time: false,
    maxPeople: false,
  });

  useEffect(() => {
    const { title, date, time, place, maxPeople } = formData;
    setFormValidation({
      title: validateTitle(title),
      place: validatePlace(place),
      date: date === '' || validateDate(date),
      time: time === '' || validateTime(time),
      maxPeople: validateMaxPeople(maxPeople),
    });
  }, [formData]);

  const updateTitle = (title: string) => {
    setFormData((prev) => ({ ...prev, title }));
  };

  const updateOfflineOrOnline = (offlineOrOnline: string) => {
    setFormData((prev) => ({ ...prev, offlineOrOnline }));
  };

  const updatePlace = (place: string) => {
    setFormData((prev) => ({ ...prev, place }));
  };

  const updateDate = (date: string) => {
    setFormData((prev) => ({ ...prev, date }));
  };

  const updateTime = (time: string) => {
    setFormData((prev) => ({ ...prev, time }));
  };

  const updateMaxPeople = (maxPeople: number) => {
    setFormData((prev) => ({ ...prev, maxPeople }));
  };

  const updateDescription = (description: string) => {
    setFormData((prev) => ({ ...prev, description }));
  };

  const { mutate: createMoim } = useAddMoim((moimId: number) => {
    navigate(`/moim/${moimId}`);
  });

  return {
    formData,
    formValidation,
    updateTitle,
    updateOfflineOrOnline,
    updatePlace,
    updateDate,
    updateTime,
    updateMaxPeople,
    updateDescription,
    createMoim,
  };
};

export default useMoimCreationForm;
