package com.example.yum_yum.data.auth.datasource;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class AuthRemoteDataSource {
    private final FirebaseAuth firebaseAuth;

    public AuthRemoteDataSource() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public Completable login(String email, String password) {
        return Completable.create(emitter -> {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable register(String email, String password) {
        return Completable.create(emitter -> {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable firebaseAuthWithGoogle(String idToken) {
        return Completable.create(emitter -> {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<String> getCurrentUserUuid() {
        return Single.create(emitter -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) emitter.onSuccess(user.getUid());
            else emitter.onError(new Exception("No user found"));
        });
    }
    public Single<String> getCurrentUserName() {
        return Single.create(emitter -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String name = (user != null && user.getDisplayName() != null) ? user.getDisplayName() : "User";
            emitter.onSuccess(name);
        });
    }
    public Single<String> getCurrentUserEmail() {
        return Single.create(emitter -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null && user.getEmail() != null) emitter.onSuccess(user.getEmail());
            else emitter.onError(new Exception("No email found"));
        });
    }

    public Completable logout() {
        return Completable.fromAction(firebaseAuth::signOut);
    }
}