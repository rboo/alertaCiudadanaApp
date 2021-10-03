package com.core.alertaciudadana.interfaces;

import com.core.alertaciudadana.models.user.Usuarios;

public interface UserInteractor {

  public void login(String user, String pass, boolean checked);
  public void logout();
  public void createAccount(Usuarios usuarios);
  public void loginAnonymous();
  public void getUserData(String uid);
}
