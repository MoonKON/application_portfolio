using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class PlayerLife : MonoBehaviour
{
    private Rigidbody2D rb;
    private Animator anim;
    private int currentHealth;
    [SerializeField] private int health = 3;
    [SerializeField] private float hurtCooldown = 0.5f;
    [SerializeField] private Image[] hearts;
    [SerializeField] private Sprite fullHeart;
    [SerializeField] private Sprite emptyHeart;
    [SerializeField] private AudioSource deadSoundEffect;
    [SerializeField] private AudioSource hurtSoundEffect;


    // Start is called before the first frame update
    void Start()
    {
        rb = GetComponent<Rigidbody2D>();
        anim = GetComponent<Animator>();
        currentHealth = health;
        UpdateHearts();
    }

    private void UpdateHearts()
    {
        Debug.Log("Use Empty Heart");
        for (int i = 0; i < hearts.Length; i++)
        {
            if (i < currentHealth)
            {
                hearts[i].sprite = fullHeart; // 当前生命值内的显示满心形
                Debug.Log("Full");
            }
            else
            {
                hearts[i].sprite = emptyHeart; // 超过当前生命值的显示空心形
                Debug.Log("empty");
            }
        }
    }

        void Update()
    {
        
    }
    public void TouchSea()
    {
        Die();
    }
    public void TakeDamage(int damageAmount)
    {
        currentHealth -= damageAmount;
        UpdateHearts();

        if (currentHealth <= 0)
        {
            Die();
        }
        else 
        {
            StartCoroutine(Hurt());
        }
    }

    private IEnumerator Hurt() 
    {
        rb.bodyType = RigidbodyType2D.Static;
        hurtSoundEffect.Play();
        anim.SetBool("getHurt", true); 
        yield return new WaitForSeconds(hurtCooldown);
        rb.bodyType = RigidbodyType2D.Dynamic;
        anim.SetBool("getHurt", false); 
    }

    private void Die()
    {
        
        rb.bodyType = RigidbodyType2D.Static;
        deadSoundEffect.Play();
        anim.SetTrigger("Death");
        LevelManager.instance.SetLastLevelIndex(); // 记录死亡前的关卡
        LevelManager.instance.LoadDeadScene(); // 切换到死亡场景
    }

}
