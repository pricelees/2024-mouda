const POLICES = {
  minimumTitleLength: 1,
  maximumTitleLength: 20,

  minimumPlaceLength: 1,
  maximumPlaceLength: 100,

  minimumMaxPeople: 1,
  maximumMaxPeople: 99,

  minimumAuthorNicknameLength: 1,
  maximumAuthorNicknameLength: 10,

  yyyymmddDashRegex: /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/,
  hhmmRegex: /^([01]\d|2[0-3]):([0-5]\d)$/,
};

export default POLICES;
