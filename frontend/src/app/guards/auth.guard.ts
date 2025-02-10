// import { inject } from '@angular/core';
// import { Router } from '@angular/router';

// export const authGuard = () => {
//   const router = inject(Router);
//   const role = localStorage.getItem('role');

//   if (role) {
//     return true; 
//   } else {
//     router.navigate(['/login']); 
//     return false;
//   }
// };
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../services/login.service';

export const authGuard = () => {
  const router = inject(Router);
  const loginService = inject(LoginService);

  const role = loginService.getRole();

  if (role) {
    return true; 
  } else {
    router.navigate(['/login']); 
    return false; 
  }
};
